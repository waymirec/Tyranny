package net.waymire.tyranny.client.net;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.client.ClientEnvironment;
import net.waymire.tyranny.client.message.MessageProperties;
import net.waymire.tyranny.client.message.MessageTopics;
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
import net.waymire.tyranny.common.mina.client.MinaTcpClient;
import net.waymire.tyranny.common.net.IpClientState;
import net.waymire.tyranny.common.net.IpProperties;
import net.waymire.tyranny.common.net.LoginserverSessionMonitor;
import net.waymire.tyranny.common.net.TcpClient;
import net.waymire.tyranny.common.net.TcpClientHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionMonitor;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.protocol.LoginserverPacketProcessorRegistryLoader;
import net.waymire.tyranny.common.protocol.LoginserverProtocolProcessor;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.protocol.TcpProtocolProcessorRegistry;

@Autoload(priority=101)
public class LoginserverClient implements TcpClient, AutoInitializable
{
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	
	private TcpClient tcpClient;
	private TcpClientHandler<LoginserverPacket> handler;
	private TcpProtocolProcessorRegistry<LoginserverPacket> registry;
	private TcpSessionMonitor sessionMonitor;
	private InetSocketAddress endpoint;
	
	public LoginserverClient()
	{

	}
	
	@Override
	public void autoInitialize()
	{
		this.registry = initProtocolProcessorRegistry();
		this.handler = new LoginserverClientHandler((ProtocolHandler<TcpSession,LoginserverPacket>)registry);
		this.tcpClient = initTcpClient();
		this.sessionMonitor = initSessionMonitor();
		
		handler.setAttribute("SERVER",  this);
		
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
	@Override
	public void connect(InetSocketAddress serverAddress)
	{
		if(!this.isConnected() && !this.isConnecting())
		{
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "Initiating Connection To Loginserver: [{0}].", serverAddress.getHostString());
			}

			Message connecting = new StandardMessage(this, MessageTopics.LOGINSERVER_CLIENT_CONNECTING);
			connecting.setProperty(MessageProperties.LOGINSERVER_SERVER_ADDRESS, serverAddress);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(connecting);

			registry.start();
			endpoint = serverAddress;
			tcpClient.connect(serverAddress);
		}
		else
		{
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "Ignoring request to connect to Loginserver {0}. Current state: {1}", ((InetSocketAddress)serverAddress).getHostString(), this.getState());
			}
		}
	}

	@Locked(mode=LockMode.WRITE)
	@Override
	public void connect(String ip, int port)
	{
		InetSocketAddress addr = new InetSocketAddress(ip, port);
		connect(addr);
	}

	@Locked(mode=LockMode.WRITE)
	@Override
	public void disconnect()
	{
		if(this.isConnected())
		{
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "Terminating Connection To Loginserver: [{0}].", endpoint.getHostString());
			}
			
			Message disconnecting = new StandardMessage(this, MessageTopics.LOGINSERVER_CLIENT_DISCONNECTING);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(disconnecting);
			
			registry.stop();
			tcpClient.disconnect();
		}
		else
		{
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "Ignoring request to disconnect. Current state: {0}", this.getState());
			}
		}
	}
	
	@Locked(mode=LockMode.READ)
	@Override
	public IpClientState getState()
	{
		return tcpClient.getState();
	}
	
	@Locked(mode=LockMode.READ)
	@Override
	public boolean isConnected()
	{
		return tcpClient.isConnected();
	}

	@Locked(mode=LockMode.READ)
	@Override
	public boolean isConnecting()
	{
		return tcpClient.isConnecting();
	}

	@Locked(mode=LockMode.WRITE)
	@Override
	public void send(Packet<? extends Opcode> message)
	{
		tcpClient.send(message);
	}
	
	@Override
	protected void finalize()
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.unload(this);
		
		if(registry.isRunning())
		{
			registry.stop();
		}
	}

	private TcpSessionMonitor initSessionMonitor()
	{
		TcpSessionMonitor monitor = new LoginserverSessionMonitor();
		return monitor;
	}
	
	private TcpProtocolProcessorRegistry<LoginserverPacket> initProtocolProcessorRegistry()
	{
		TcpProtocolProcessorRegistry<LoginserverPacket> registry = new TcpProtocolProcessorRegistry<>();
		File processorPath = new File(AppRegistry.getInstance().retrieve(ClientEnvironment.class).getFullProcessorPath());
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

	private TcpClient initTcpClient()
	{
		MinaTcpClient<LoginserverPacket> client = new MinaTcpClient<LoginserverPacket>();
		client.setHandler(handler);
		client.setProtocolCodecFactory(new LoginserverPacketCodecFactory());
		
		IpProperties properties = new IpProperties();
		client.setProperties(properties);
		
		return client;
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_LOGIN_REQUEST)
	private void onLoginRequestMessage(Message message)
	{
		this.connect("localhost", 12346);
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_CONNECT_FAILED)
	private void onLoginserverClientConnectFailed(Message message)
	{
		if(registry.isRunning())
		{
			registry.stop();
		}
	}

	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_CONNECT_SUCCESS)
	private void onLoginserverClientConnectSuccess(Message message)
	{
	}

	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_SUCCESS)
	private void onLoginserverClientAuthenticated(Message message)
	{
		sessionMonitor.add((TcpSession)message.getSource());
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_DISCONNECTED)
	private void onLoginserverClientDisconnected(Message message)
	{
		if(registry.isRunning())
		{
			registry.stop();
		}
		sessionMonitor.remove((TcpSession)message.getSource());
	}
} 
