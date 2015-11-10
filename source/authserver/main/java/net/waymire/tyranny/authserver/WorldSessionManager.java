package net.waymire.tyranny.authserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.authserver.message.MessageProperties;
import net.waymire.tyranny.authserver.message.MessageTopics;
import net.waymire.tyranny.authserver.net.AuthControlServerSessionAttributes;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.World;
import net.waymire.tyranny.common.annotation.LockField;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessageProcessor;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;

@Autoload(priority=500)
public class WorldSessionManager implements AutoInitializable
{
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private final Map<GUID,TcpSession> sessions = new HashMap<>();
	
	@Override
	public void autoInitialize()
	{
		AppRegistry registry = AppRegistry.getInstance();
		registry.register(WorldSessionManager.class,this);
		
		MessageManager messageManager = registry.retrieve(MessageManager.class);
		messageManager.load(this);
	}
	
	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().unregister(WorldSessionManager.class);
	}

	@Locked(mode=LockMode.READ)
	public TcpSession getWorldSession(GUID sessionId)
	{
		return sessions.get(sessionId);
	}
	
	@Locked(mode=LockMode.READ)
	public List<TcpSession> getWorldSessions()
	{
		return new ArrayList<TcpSession>(sessions.values());
	}
	
	@Locked(mode=LockMode.READ)
	public TcpSession getAvailableWorldSession()
	{
		if(sessions.isEmpty())
		{
			return null;
		}
		return sessions.isEmpty() ? null : (TcpSession)sessions.values().toArray()[0];
	}
	
	@Locked(mode=LockMode.WRITE)
	private void add(GUID worldId, TcpSession session)
	{
		sessions.put(worldId, session);
	}
	
	@Locked(mode=LockMode.WRITE)
	private void remove(GUID worldId)
	{
		sessions.remove(worldId);
	}
	
	@MessageProcessor(topic=MessageTopics.AUTHCONTROL_SERVER_CLIENT_AUTHENTICATED)
	private void onAuthControlClientConnectedMessage(Message message)
	{
		TcpSession session = (TcpSession)message.getSource();
		World world = (World)session.getAttribute(AuthControlServerSessionAttributes.WORLD);
		if(world != null)
		{
			this.add(world.getGUID(), session);
			
			Message worldAvailable = new StandardMessage(session, MessageTopics.WORLD_AVAILABLE);
			worldAvailable.setProperty(MessageProperties.WORLD_ID, world.getGUID());
			worldAvailable.setProperty(MessageProperties.WORLD, world);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(worldAvailable);
		}
	}
	
	@MessageProcessor(topic=MessageTopics.AUTHCONTROL_SERVER_CLIENT_DISCONNECTED)
	private void onAuthControlClientDisconnectedMessage(Message message)
	{
		GUID worldId = (GUID)message.getProperty(MessageProperties.WORLD_ID);
		if(worldId != null)
		{
			this.remove(worldId);
		
			TcpSession session = (TcpSession)message.getSource();
			Message worldUnavailable = new StandardMessage(session, MessageTopics.WORLD_AVAILABLE);
			worldUnavailable.setProperty(MessageProperties.WORLD_ID, worldId);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(worldUnavailable);
		}
	}	
}
