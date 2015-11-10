package net.waymire.tyranny.worldserver.net;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.annotation.LockField;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.common.file.FilesystemException;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessageProcessor;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.mina.WorldserverPacketCodecFactory;
import net.waymire.tyranny.common.mina.server.MinaTcpServer;
import net.waymire.tyranny.common.net.IpProperties;
import net.waymire.tyranny.common.net.IpSessionMonitor;
import net.waymire.tyranny.common.net.TcpServer;
import net.waymire.tyranny.common.net.TcpServerHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionMonitor;
import net.waymire.tyranny.common.net.WorldserverSessionMonitor;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.protocol.TcpProtocolProcessorRegistry;
import net.waymire.tyranny.common.protocol.WorldserverPacket;
import net.waymire.tyranny.common.protocol.WorldserverPacketProcessorRegistryLoader;
import net.waymire.tyranny.common.protocol.WorldserverProtocolProcessor;
import net.waymire.tyranny.worldserver.WorldserverEnvironment;
import net.waymire.tyranny.worldserver.configuration.WorldConfigKey;
import net.waymire.tyranny.worldserver.configuration.WorldserverConfig;
import net.waymire.tyranny.worldserver.message.MessageTopics;

@Autoload(priority=100)
public class Worldserver implements TcpServer, AutoInitializable
{
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final AtomicBoolean running = new AtomicBoolean(false);
	
	private TcpServer tcpServer;
	private TcpServerHandler<WorldserverPacket> handler;
	private TcpProtocolProcessorRegistry<WorldserverPacket> registry;
	private IpSessionMonitor<TcpSession> sessionMonitor;
	private WorldserverConfig config;
	
	public Worldserver()
	{

	}
	
	@Override
	public void autoInitialize()
	{
		this.config = (WorldserverConfig)AppRegistry.getInstance().retrieve(WorldserverConfig.class);
		this.registry = initProtocolProcessorRegistry();
		this.handler = new WorldserverHandler((ProtocolHandler<TcpSession,WorldserverPacket>)registry,this);
		this.tcpServer = initTcpServer();
		this.sessionMonitor = initSessionMonitor();
		
		handler.setAttribute("SERVER",  this);
		handler.setAttribute("SESSION_MONITOR",  sessionMonitor);
		
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.load(this);
	}

	@Override
	public void autoDeinitialize()
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.unload(this);
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.SYSTEM_STARTING)
	private void onSystemStarting(Message message)
	{
		if(running.get())
		{
			this.shutdown();
		}
			
		this.startup();
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.SYSTEM_STOPPING)
	private void onSystemStopping(Message message)
	{
		this.shutdown();
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public boolean startup() 
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);

		Message message = new StandardMessage(this, MessageTopics.WORLDSERVER_STARTING);
		messageManager.publish(message);

		LogHelper.info(this, "Starting Worldserver...");
		
		running.set(true);
		sessionMonitor.start();
		registry.start();
		tcpServer.startup();
		
		LogHelper.info(this, "Worldserver started.");
		
		message = new StandardMessage(this, MessageTopics.WORLDSERVER_STARTED);
		messageManager.publish(message);
		
		return true;
	}

	@Override
	@Locked(mode=LockMode.WRITE)
	public boolean shutdown() 
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		
		LogHelper.info(this, "Stopping Worldserver...");

		Message message = new StandardMessage(this, MessageTopics.WORLDSERVER_STOPPING);
		messageManager.publish(message);
		
		running.set(false);
		sessionMonitor.stop();
		tcpServer.shutdown();
		registry.stop();
		
		LogHelper.info(this, "Worldserver stopped.");
		
		message = new StandardMessage(this, MessageTopics.WORLDSERVER_STOPPED);
		messageManager.publish(message);
		
		return true;
	}

	@Override
	protected void finalize()
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.unload(this);
	}

	private IpSessionMonitor<TcpSession> initSessionMonitor()
	{
		TcpSessionMonitor monitor = new WorldserverSessionMonitor();
		return monitor;
	}
	
	private TcpServer initTcpServer()
	{
		MinaTcpServer<WorldserverPacket> server = new MinaTcpServer<>();
		server.setHandler(handler);
		
		String ip = config.getValue(WorldConfigKey.WORLD_LISTENER_IP);
		int port = config.getValueAs(Integer.class, WorldConfigKey.WORLD_LISTENER_PORT);
		server.setAddress(new InetSocketAddress(ip,port));

		server.setProtocolCodecFactory(new WorldserverPacketCodecFactory());
		server.setProperties(initProperties(config));
		return server;
	}
	
	private IpProperties initProperties(WorldserverConfig config)
	{
		IpProperties properties = new IpProperties();
		return properties;
	}
	
	private TcpProtocolProcessorRegistry<WorldserverPacket> initProtocolProcessorRegistry()
	{
		TcpProtocolProcessorRegistry<WorldserverPacket> registry = new TcpProtocolProcessorRegistry<>();
		File processorPath = new File(AppRegistry.getInstance().retrieve(WorldserverEnvironment.class).getFullProcessorPath());
		WorldserverPacketProcessorRegistryLoader loader = new WorldserverPacketProcessorRegistryLoader(registry, WorldserverProtocolProcessor.class.getName());
		
		try
		{
			loader.load(processorPath);
		}
		catch(FilesystemException filesystemException)
		{
			LogHelper.severe(this, "Failed to read from processor directory [{0}]. Exiting...", processorPath.getAbsolutePath());
		}
		
		return registry;
	}
}
