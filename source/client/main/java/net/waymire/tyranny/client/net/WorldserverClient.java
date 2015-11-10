package net.waymire.tyranny.client.net;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.client.ClientEnvironment;
import net.waymire.tyranny.client.message.MessageProperties;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.client.message.MovementAction;
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
import net.waymire.tyranny.common.mina.WorldserverPacketCodecFactory;
import net.waymire.tyranny.common.mina.client.MinaTcpClient;
import net.waymire.tyranny.common.net.IpClientState;
import net.waymire.tyranny.common.net.IpProperties;
import net.waymire.tyranny.common.net.TcpClient;
import net.waymire.tyranny.common.net.TcpClientHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionMonitor;
import net.waymire.tyranny.common.net.WorldserverSessionMonitor;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.protocol.TcpProtocolProcessorRegistry;
import net.waymire.tyranny.common.protocol.WorldserverOpcode;
import net.waymire.tyranny.common.protocol.WorldserverPacket;
import net.waymire.tyranny.common.protocol.WorldserverPacketProcessorRegistryLoader;
import net.waymire.tyranny.common.protocol.WorldserverProtocolProcessor;

@Autoload(priority=102)
public class WorldserverClient implements TcpClient, AutoInitializable 
{
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	
	private TcpClient tcpClient;
	private TcpClientHandler<WorldserverPacket> handler;
	private TcpProtocolProcessorRegistry<WorldserverPacket> registry;
	private TcpSessionMonitor sessionMonitor;
	private InetSocketAddress endpoint;
	private GUID identToken = null;
	
	private static final Map<MovementAction,WorldserverOpcode> moveDirectionToOpcode = new HashMap<>();
	static
	{
		moveDirectionToOpcode.put(MovementAction.FORWARD, WorldserverOpcode.PLAYER_MOVE_FORWARD);
		moveDirectionToOpcode.put(MovementAction.BACKWARD, WorldserverOpcode.PLAYER_MOVE_BACKWARD);
		moveDirectionToOpcode.put(MovementAction.LEFT, WorldserverOpcode.PLAYER_MOVE_LEFT);
		moveDirectionToOpcode.put(MovementAction.RIGHT, WorldserverOpcode.PLAYER_MOVE_RIGHT);
		moveDirectionToOpcode.put(MovementAction.JUMP, WorldserverOpcode.PLAYER_MOVE_JUMP);
		moveDirectionToOpcode.put(MovementAction.ROTATE_LEFT, WorldserverOpcode.PLAYER_MOVE_ROTATE_LEFT);
		moveDirectionToOpcode.put(MovementAction.ROTATE_RIGHT, WorldserverOpcode.PLAYER_MOVE_ROTATE_RIGHT);
	}
	
	public WorldserverClient()
	{
	}
	
	@Override
	public void autoInitialize()
	{
		this.registry = initProtocolProcessorRegistry();
		this.handler = new WorldserverClientHandler((ProtocolHandler<TcpSession,WorldserverPacket>)registry);
		this.tcpClient = initTcpClient();
		this.sessionMonitor = initSessionMonitor();
		
		handler.setAttribute("SERVER",  this);

		AppRegistry.getInstance().retrieve(MessageManager.class).load(this);
		AppRegistry.getInstance().register(WorldserverClient.class, this);
	}
	
	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().retrieve(MessageManager.class).unload(this);
		AppRegistry.getInstance().unregister(WorldserverClient.class);
	}
	
	@Locked(mode=LockMode.WRITE)
	@Override
	public void connect(InetSocketAddress serverAddress)
	{
		if(!this.isConnected() && !this.isConnecting())
		{
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "Initiating Connection To Worldserver: [{0}].", serverAddress.getHostString());
			}

			Message connecting = new StandardMessage(this, MessageTopics.WORLDSERVER_CLIENT_CONNECTING);
			connecting.setProperty(MessageProperties.WORLDSERVER_SERVER_ADDRESS, serverAddress);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(connecting);

			registry.start();
			endpoint = serverAddress;
			tcpClient.connect(serverAddress);
		}
		else
		{
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "Ignoring request to connect to Worldserver {0}. Current state: {1}", ((InetSocketAddress)serverAddress).getHostString(), this.getState());
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
				LogHelper.debug(this, "Terminating Connection To Worldserver: [{0}].", endpoint.getHostString());
			}
			
			Message disconnecting = new StandardMessage(this, MessageTopics.WORLDSERVER_CLIENT_DISCONNECTING);
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
		TcpSessionMonitor monitor = new WorldserverSessionMonitor();
		return monitor;
	}
	
	private TcpProtocolProcessorRegistry<WorldserverPacket> initProtocolProcessorRegistry()
	{
		TcpProtocolProcessorRegistry<WorldserverPacket> registry = new TcpProtocolProcessorRegistry<>();
		File processorPath = new File(AppRegistry.getInstance().retrieve(ClientEnvironment.class).getFullProcessorPath());
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

	private TcpClient initTcpClient()
	{
		MinaTcpClient<WorldserverPacket> client = new MinaTcpClient<WorldserverPacket>();
		client.setHandler(handler);
		client.setProtocolCodecFactory(new WorldserverPacketCodecFactory());
		
		IpProperties properties = new IpProperties();
		client.setProperties(properties);
		
		return client;
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_CONNECT)
	private void onWorldserverConnectRequest(Message message)
	{
		InetSocketAddress address = (InetSocketAddress)message.getProperty(MessageProperties.WORLDSERVER_SERVER_ADDRESS);
		this.identToken = (GUID)message.getProperty(MessageProperties.WORLDSERVER_AUTH_TOKEN);		
		this.connect(address);
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_CONNECT_FAILED)
	private void onWorldserverConnectFailed(Message message)
	{
		if(registry.isRunning())
		{
			registry.stop();
		}
	}

	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_CONNECT_SUCCESS)
	private void onWorldserverConnectSuccess(Message message)
	{
		TcpSession session = (TcpSession)message.getSource();
		session.setAttribute(WorldserverClientSessionAttributes.WORLDSERVERCLIENT_IDENT_TOKEN, identToken);
		final WorldserverPacket ident = new WorldserverPacket(WorldserverOpcode.IDENT);
		ident.putLong(identToken.getMostSignificantBits());
		ident.putLong(identToken.getLeastSignificantBits());
		ident.prepare();
		session.send(ident);
	}

	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_IDENT_SUCCESS)
	private void onWorldserverIdentSuccess(Message message)
	{
		sessionMonitor.add((TcpSession)message.getSource());		
	}

	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_IDENT_FAILED)
	private void onWorldserverIdentFailed(Message message)
	{
		if(registry.isRunning())
		{
			registry.stop();
		}

		TcpSession session = (TcpSession)message.getSource();
		session.close();
	}

	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_DISCONNECTED)
	private void onLoginserverClientDisconnected(Message message)
	{
		if(registry.isRunning())
		{
			registry.stop();
		}
		sessionMonitor.remove((TcpSession)message.getSource());
	}
	
	/*
	@MessageProcessor(topic=MessageTopics.PLAYER_MOVE)
	private void onPlayerMoveMessage(Message message)
	{
		MovementAction direction = (MovementAction)message.getProperty(MessageProperties.PLAYER_MOVE_DIRECTION);
		if(moveDirectionToOpcode.containsKey(direction))
		{
			Boolean status = (Boolean)message.getProperty(MessageProperties.STATUS);
			WorldserverPacket packet = new WorldserverPacket(moveDirectionToOpcode.get(direction));
			packet.putBoolean(status);
			packet.prepare();
			this.send(packet);
		}
	}
	*/
}