package net.waymire.tyranny.authserver.net;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.authserver.AuthserverEnvironment;
import net.waymire.tyranny.authserver.message.MessageTopics;
import net.waymire.tyranny.authserver.configuration.AuthConfigKey;
import net.waymire.tyranny.authserver.configuration.AuthserverConfig;
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
import net.waymire.tyranny.common.mina.LoginserverPacketCodecFactory;
import net.waymire.tyranny.common.mina.server.MinaTcpServer;
import net.waymire.tyranny.common.net.IpProperties;
import net.waymire.tyranny.common.net.LoginserverSessionMonitor;
import net.waymire.tyranny.common.net.TcpServer;
import net.waymire.tyranny.common.net.TcpServerHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionMonitor;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.protocol.LoginserverPacketProcessorRegistryLoader;
import net.waymire.tyranny.common.protocol.LoginserverProtocolProcessor;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.protocol.TcpProtocolProcessorRegistry;

@Autoload(priority=105)
public class Loginserver implements TcpServer, AutoInitializable
{
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final AtomicBoolean running = new AtomicBoolean(false);
	
	private TcpServer tcpServer;
	private TcpServerHandler<LoginserverPacket> handler;
	private TcpProtocolProcessorRegistry<LoginserverPacket> registry;
	private TcpSessionMonitor sessionMonitor;
	private AuthserverConfig config;
	
	public Loginserver()
	{

	}
	
	@Override
	public void autoInitialize()
	{
		this.config = (AuthserverConfig)AppRegistry.getInstance().retrieve(AuthserverConfig.class);
		this.registry = initProtocolProcessorRegistry();
		this.handler = new LoginserverHandler((ProtocolHandler<TcpSession,LoginserverPacket>)registry);
		this.tcpServer = initTcpServer();
		this.sessionMonitor = initSessionMonitor();
		
		handler.setAttribute("SERVER",  this);
		
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.load(this);
	}
	
	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().unregister(MessageManager.class);
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.AUTHCONTROL_SERVER_STARTED)
	private void onAuthControlServerStarted(Message message)
	{
		if(running.get())
		{
			this.shutdown();
		}
			
		this.startup();
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.AUTHCONTROL_SERVER_STOPPED)
	private void onAuthControlServerStopped(Message message)
	{
		this.shutdown();
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public boolean startup() 
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);

		Message message = new StandardMessage(this, MessageTopics.LOGINSERVER_STARTING);
		messageManager.publish(message);

		LogHelper.info(this, "Starting Loginserver...");
		
		running.set(true);
		sessionMonitor.start();
		registry.start();
		tcpServer.startup();
		
		LogHelper.info(this, "Loginserver started.");
		
		message = new StandardMessage(this, MessageTopics.LOGINSERVER_STARTED);
		messageManager.publish(message);
		
		return true;
	}

	@Override
	@Locked(mode=LockMode.WRITE)
	public boolean shutdown() 
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		
		LogHelper.info(this, "Stopping Loginserver...");

		Message message = new StandardMessage(this, MessageTopics.LOGINSERVER_STOPPING);
		messageManager.publish(message);
		
		running.set(false);
		sessionMonitor.stop();
		tcpServer.shutdown();
		registry.stop();
		
		LogHelper.info(this, "Loginerver stopped.");
		
		message = new StandardMessage(this, MessageTopics.LOGINSERVER_STOPPED);
		messageManager.publish(message);
		
		return true;
	}

	@Override
	protected void finalize()
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.unload(this);
	}

	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTHENTICATED)
	private void onClientAuthenticatedMessage(Message message)
	{
		sessionMonitor.add((TcpSession)message.getSource());
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_DISCONNECTED)
	private void onClientDisconnectedMessage(Message message)
	{
		sessionMonitor.remove((TcpSession)message.getSource());
	}

	private TcpSessionMonitor initSessionMonitor()
	{
		TcpSessionMonitor monitor = new LoginserverSessionMonitor();
		return monitor;
	}
	
	private TcpServer initTcpServer()
	{
		MinaTcpServer<LoginserverPacket> server = new MinaTcpServer<>();
		server.setHandler(handler);
		
		String ip = config.getValue(AuthConfigKey.LOGIN_LISTENER_IP);
		int port = config.getValueAs(Integer.class, AuthConfigKey.LOGIN_LISTENER_PORT);
		server.setAddress(new InetSocketAddress(ip,port));

		server.setProtocolCodecFactory(new LoginserverPacketCodecFactory());
		server.setProperties(initProperties(config));
		return server;
	}
	
	private IpProperties initProperties(AuthserverConfig config)
	{
		IpProperties properties = new IpProperties();
		return properties;
	}
	
	private TcpProtocolProcessorRegistry<LoginserverPacket> initProtocolProcessorRegistry()
	{
		TcpProtocolProcessorRegistry<LoginserverPacket> registry = new TcpProtocolProcessorRegistry<>();
		File processorPath = new File(AppRegistry.getInstance().retrieve(AuthserverEnvironment.class).getFullProcessorPath());
		LoginserverPacketProcessorRegistryLoader loader = new LoginserverPacketProcessorRegistryLoader(registry, LoginserverProtocolProcessor.class.getName());
		
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
