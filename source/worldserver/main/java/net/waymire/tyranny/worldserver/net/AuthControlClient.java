package net.waymire.tyranny.worldserver.net;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.Environment;
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
import net.waymire.tyranny.common.mina.client.MinaTcpClient;
import net.waymire.tyranny.common.net.AuthControlSessionMonitor;
import net.waymire.tyranny.common.net.IpClientState;
import net.waymire.tyranny.common.net.IpConstants;
import net.waymire.tyranny.common.net.IpProperties;
import net.waymire.tyranny.common.net.TcpClient;
import net.waymire.tyranny.common.net.TcpClientHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionMonitor;
import net.waymire.tyranny.common.protocol.AuthControlPacket;
import net.waymire.tyranny.common.protocol.AuthControlPacketProcessorRegistryLoader;
import net.waymire.tyranny.common.protocol.AuthControlProtocolProcessor;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.protocol.TcpProtocolProcessorRegistry;
import net.waymire.tyranny.common.util.StringUtil;
import net.waymire.tyranny.worldserver.WorldserverEnvironment;
import net.waymire.tyranny.worldserver.configuration.WorldConfigKey;
import net.waymire.tyranny.worldserver.configuration.WorldserverConfig;
import net.waymire.tyranny.worldserver.message.MessageProperties;
import net.waymire.tyranny.worldserver.message.MessageTopics;

@Autoload(priority=101)
public class AuthControlClient implements TcpClient, AutoInitializable
{
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	
	private TcpClient tcpClient;
	private TcpClientHandler<AuthControlPacket> handler;
	private TcpProtocolProcessorRegistry<AuthControlPacket> registry;
	private TcpSessionMonitor sessionMonitor;
	private InetSocketAddress endpoint;
	
	public AuthControlClient()
	{

	}
	
	@Override
	public void autoInitialize()
	{
		this.registry = initProtocolProcessorRegistry();
		this.handler = new AuthControlClientHandler((ProtocolHandler<TcpSession,AuthControlPacket>)registry);
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
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_STARTED)
	private void onAuthControlServerStarted(Message message)
	{
		if(this.isConnected())
		{
			this.disconnect();
		}
		
		WorldserverConfig config = (WorldserverConfig)AppRegistry.getInstance().retrieve(WorldserverConfig.class);
		String ip = config.getValue(WorldConfigKey.CONTROL_SERVER_IP);
		Integer port = config.getValueAs(Integer.class,  WorldConfigKey.CONTROL_SERVER_PORT);
		this.connect(ip, port);
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_STOPPED)
	private void onAuthControlServerStopped(Message message)
	{
		if(this.isConnected())
		{
			this.disconnect();
		}
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.AUTHCONTROL_CLIENT_AUTH_SUCCESS)
	private void onAuthControlClientAuthenticated(Message message)
	{
		sessionMonitor.add((TcpSession)message.getSource());
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.AUTHCONTROL_CLIENT_DISCONNECTED)
	private void onAuthControlClientDisconnected(Message message)
	{
		if(registry.isRunning())
		{
			registry.stop();
		}

		sessionMonitor.remove((TcpSession)message.getSource());
		try { Thread.sleep(3000); } catch(InterruptedException ie) { }
		this.connect(endpoint);
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.AUTHCONTROL_CLIENT_CONNECTION_FAILED)
	private void onAuthControlClientConnectFailed(Message message)
	{
		if(registry.isRunning())
		{
			registry.stop();
		}

		try { Thread.sleep(15000); } catch(InterruptedException ie) { }
		this.connect(endpoint);
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
			
			Message connecting = new StandardMessage(this, MessageTopics.AUTHCONTROL_CLIENT_CONNECTING);
			connecting.setProperty(MessageProperties.SERVER_ADDRESS, serverAddress);
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
			
			Message disconnecting = new StandardMessage(this, MessageTopics.AUTHCONTROL_CLIENT_DISCONNECTING);
			disconnecting.setProperty(MessageProperties.SERVER_ADDRESS, this.endpoint);
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
		TcpSessionMonitor monitor = new AuthControlSessionMonitor();
		return monitor;
	}
	
	private TcpProtocolProcessorRegistry<AuthControlPacket> initProtocolProcessorRegistry()
	{
		TcpProtocolProcessorRegistry<AuthControlPacket> registry = new TcpProtocolProcessorRegistry<>();
		File processorPath = new File(AppRegistry.getInstance().retrieve(WorldserverEnvironment.class).getFullProcessorPath());
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
	
	private TcpClient initTcpClient()
	{
		MinaTcpClient<AuthControlPacket> client = new MinaTcpClient<AuthControlPacket>();
		client.setHandler(handler);
		client.setProtocolCodecFactory(new AuthControlPacketCodecFactory());
		
		WorldserverEnvironment env = AppRegistry.getInstance().retrieve(WorldserverEnvironment.class);
		String sslDir = env.getFullDataPath();
		
		IpProperties properties = new IpProperties();
		properties.setSslKeystore(StringUtil.concat(sslDir, Environment.getFileSeparator(), IpConstants.SSL_KEYSTORE_FILENAME));
		properties.setSslTruststore(StringUtil.concat(sslDir,Environment.getFileSeparator(), IpConstants.SSL_TRUSTSTORE_FILENAME));
		properties.setSslPassword(IpConstants.SSL_STORE_PASSWORD);
				
		client.setProperties(properties);
		
		return client;
	}	
}