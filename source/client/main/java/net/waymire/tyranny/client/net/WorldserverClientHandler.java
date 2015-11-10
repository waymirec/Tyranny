package net.waymire.tyranny.client.net;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.annotation.LockField;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpClientHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.protocol.WorldserverPacket;
import net.waymire.tyranny.common.util.ExceptionUtil;

public class WorldserverClientHandler implements TcpClientHandler<WorldserverPacket> 
{
	private final Map<String,Object> attributes = new HashMap<String,Object>();
	
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private ProtocolHandler<TcpSession,WorldserverPacket> protocolHandler;

	public WorldserverClientHandler(ProtocolHandler<TcpSession,WorldserverPacket> protocolHandler)
	{
		this.protocolHandler = protocolHandler;
		AppRegistry.getInstance().retrieve(MessageManager.class).load(this);
	}
	
	public void setProtocolHandler(ProtocolHandler<TcpSession,WorldserverPacket> protocolHandler)
	{
		this.protocolHandler = protocolHandler;
	}
	
	@Override
	public void onSessionOpened(TcpSession session)
	{
		session.setAttribute(WorldserverClientSessionAttributes.WORLDSERVERCLIENT_SESSION_STATE, WorldserverClientSessionState.CONNECTED);
		
		session.setAuthenticated(false);
		
		session.setAttribute(TcpSessionAttributes.LAST_PING_TX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PING_TX_TIME,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_TIME,(long)0);
		
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_TIME,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_TIME,Long.valueOf(0));

		Message connected = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_CONNECT_SUCCESS);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(connected);
	}
	
	@Override
	public void onSessionClosed(TcpSession session)
	{
		LogHelper.info(this,"Lost connection to server: [{0}].", ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		synchronized(session)
		{
			session.setAttribute(WorldserverClientSessionAttributes.WORLDSERVERCLIENT_SESSION_STATE, WorldserverClientSessionState.NULL);
		}

		Message disconnected = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_DISCONNECTED);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(disconnected);		
	}
	
	@Override
	public void onConnectFailed(TcpSession session) 
	{
		Message connectionFailed = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_CONNECT_FAILED);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(connectionFailed);
	}

	@Override
	public void onSessionIdle(TcpSession session) 
	{
		Message idle = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_SESSION_IDLE);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(idle);
	}
	
	@Override
	@Locked(mode=LockMode.READ)
	public void onMessageReceived(TcpSession session,WorldserverPacket message) 
	{
		if(protocolHandler != null)
		{
			protocolHandler.handle(session, message);
		}
	}

	@Override
	public void onException(TcpSession session, Throwable t) 
	{
		LogHelper.severe(this, "EXCEPTION [{0}]: {1}", session, ExceptionUtil.getReason(t));
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	@Override
	public void setAttribute(String key, Object value)
	{
		attributes.put(key, value);
	}
	
	@Override
	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}
	
	protected void finalize()
	{
		AppRegistry.getInstance().retrieve(MessageManager.class).unload(this);
	}	
}
