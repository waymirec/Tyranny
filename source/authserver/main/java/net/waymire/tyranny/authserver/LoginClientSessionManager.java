package net.waymire.tyranny.authserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.authserver.message.MessageTopics;
import net.waymire.tyranny.authserver.net.LoginserverSessionAttributes;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.annotation.LockField;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessageProcessor;
import net.waymire.tyranny.common.net.TcpSession;

@Autoload(priority=500)
public class LoginClientSessionManager implements AutoInitializable
{
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private final Map<GUID,TcpSession> sessions = new HashMap<>();
	
	@Override
	public void autoInitialize()
	{
		AppRegistry registry = AppRegistry.getInstance();
		registry.register(LoginClientSessionManager.class,this);
		
		MessageManager messageManager = registry.retrieve(MessageManager.class);
		messageManager.load(this);
	}
	
	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().unregister(LoginClientSessionManager.class);
	}
	
	@Locked(mode=LockMode.READ)
	public TcpSession getLoginClientSession(GUID accountId)
	{
		return sessions.get(accountId);
	}
	
	@Locked(mode=LockMode.READ)
	public List<TcpSession> getLoginClientSessions()
	{
		return new ArrayList<TcpSession>(sessions.values());
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
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTHENTICATED)
	private void onLoginClientConnectedMessage(Message message)
	{
		TcpSession session = (TcpSession)message.getSource();
		GUID accountId = (GUID)session.getAttribute(LoginserverSessionAttributes.PLAYER_ACCT_ID);
		this.add(accountId, session);
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_DISCONNECTED)
	private void onLoginClientDisconnectedMessage(Message message)
	{
		TcpSession session = (TcpSession)message.getSource();
		GUID accountId = (GUID)session.getAttribute(LoginserverSessionAttributes.PLAYER_ACCT_ID);
		this.remove(accountId);		
	}
}
