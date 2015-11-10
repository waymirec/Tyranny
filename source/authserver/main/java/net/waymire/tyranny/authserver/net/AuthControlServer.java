package net.waymire.tyranny.authserver.net;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
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
import net.waymire.tyranny.common.mina.AuthControlPacketCodecFactory;
import net.waymire.tyranny.common.mina.server.MinaTcpServer;
import net.waymire.tyranny.common.net.AuthControlSessionMonitor;
import net.waymire.tyranny.common.net.IpProperties;
import net.waymire.tyranny.common.net.TcpServer;
import net.waymire.tyranny.common.net.TcpServerHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionMonitor;
import net.waymire.tyranny.common.protocol.AuthControlOpcode;
import net.waymire.tyranny.common.protocol.AuthControlProtocolProcessor;
import net.waymire.tyranny.common.protocol.AuthControlPacket;
import net.waymire.tyranny.common.protocol.AuthControlPacketProcessorRegistryLoader;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.protocol.TcpProtocolProcessorRegistry;
import net.waymire.tyranny.common.util.InetAddressUtil;
import net.waymire.tyranny.authserver.AuthserverEnvironment;
import net.waymire.tyranny.authserver.WorldSessionManager;
import net.waymire.tyranny.authserver.message.MessageProperties;
import net.waymire.tyranny.authserver.message.MessageTopics;
import net.waymire.tyranny.authserver.configuration.AuthConfigKey;
import net.waymire.tyranny.authserver.configuration.AuthserverConfig;

@Autoload(priority=100)
public class AuthControlServer implements TcpServer, AutoInitializable
{
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final AtomicBoolean running = new AtomicBoolean(false);

	private TcpServer tcpServer;
	private TcpServerHandler<AuthControlPacket> handler;
	private TcpProtocolProcessorRegistry<AuthControlPacket> registry;
	private TcpSessionMonitor sessionMonitor;
	private AuthserverConfig config;
	
	public AuthControlServer()
	{

	}
	
	@Override
	public void autoInitialize()
	{
		this.config = (AuthserverConfig)AppRegistry.getInstance().retrieve(AuthserverConfig.class);
		this.registry = initProtocolProcessorRegistry();
		this.handler = new AuthControlServerHandler((ProtocolHandler<TcpSession,AuthControlPacket>)registry);
		this.tcpServer = initTcpServer();
		this.sessionMonitor = initSessionMonitor();
		
		handler.setAttribute("SERVER",  this);
		
		AppRegistry registry = AppRegistry.getInstance();
		
		registry.register(WorldSessionManager.class, new WorldSessionManager());

		MessageManager messageManager = registry.retrieve(MessageManager.class);
		messageManager.load(this);
	}

	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().unregister(MessageManager.class);
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
	
	@MessageProcessor(topic=MessageTopics.AUTHCONTROL_SERVER_CLIENT_AUTHENTICATED)
	private void onClientAuthenticatedMessage(Message message)
	{
		sessionMonitor.add((TcpSession)message.getSource());
	}
	
	@MessageProcessor(topic=MessageTopics.AUTHCONTROL_SERVER_CLIENT_DISCONNECTED)
	private void onClientDisconnected(Message message)
	{
		sessionMonitor.remove((TcpSession)message.getSource());
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public boolean startup() 
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);

		Message message = new StandardMessage(this, MessageTopics.AUTHCONTROL_SERVER_STARTING);
		messageManager.publish(message);

		LogHelper.info(this, "Starting Auth Control server...");
		
		running.set(true);
		sessionMonitor.start();
		registry.start();
		tcpServer.startup();
		
		LogHelper.info(this, "Auth Control server started.");
		
		message = new StandardMessage(this, MessageTopics.AUTHCONTROL_SERVER_STARTED);
		messageManager.publish(message);
		
		return true;
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public boolean shutdown() 
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		
		LogHelper.info(this, "Stopping Auth Control server...");

		Message message = new StandardMessage(this, MessageTopics.AUTHCONTROL_SERVER_STOPPING);
		messageManager.publish(message);
		
		running.set(false);
		sessionMonitor.stop();
		tcpServer.shutdown();
		registry.stop();
		
		LogHelper.info(this, "Auth Control server stopped.");
		
		message = new StandardMessage(this, MessageTopics.AUTHCONTROL_SERVER_STOPPED);
		messageManager.publish(message);
		
		return true;
	}

	@Override
	protected void finalize()
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.unload(this);
	}
	
	private TcpSessionMonitor initSessionMonitor()
	{
		TcpSessionMonitor monitor = new AuthControlSessionMonitor();
		return monitor;
	}
	
	private TcpServer initTcpServer()
	{
		MinaTcpServer<AuthControlPacket> server = new MinaTcpServer<>();
		server.setHandler(handler);
		
		String ip = config.getValue(AuthConfigKey.CONTROL_LISTENER_IP);
		Integer port = config.getValueAs(Integer.class, AuthConfigKey.CONTROL_LISTENER_PORT);
		server.setAddress(new InetSocketAddress(ip,port));

		server.setProtocolCodecFactory(new AuthControlPacketCodecFactory());
		server.setProperties(initProperties(config));
		return server;
	}
	
	private IpProperties initProperties(AuthserverConfig config)
	{
		IpProperties properties = new IpProperties();
		properties.setIdleTime(300);
		return properties;
	}
	
	private TcpProtocolProcessorRegistry<AuthControlPacket> initProtocolProcessorRegistry()
	{
		TcpProtocolProcessorRegistry<AuthControlPacket> registry = new TcpProtocolProcessorRegistry<>();
		File processorPath = new File(AppRegistry.getInstance().retrieve(AuthserverEnvironment.class).getFullProcessorPath());
		AuthControlPacketProcessorRegistryLoader loader = new AuthControlPacketProcessorRegistryLoader(registry, AuthControlProtocolProcessor.class.getName());
		
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