package net.waymire.tyranny.worldserver.net;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.TyrannyConstants;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpClientHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.AuthControlOpcode;
import net.waymire.tyranny.common.protocol.AuthControlPacket;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.util.ExceptionUtil;
import net.waymire.tyranny.common.util.InetAddressUtil;
import net.waymire.tyranny.worldserver.configuration.WorldConfigKey;
import net.waymire.tyranny.worldserver.configuration.WorldserverConfig;
import net.waymire.tyranny.worldserver.message.MessageTopics;

public class AuthControlClientHandler implements TcpClientHandler<AuthControlPacket> 
{
	private final Map<String,Object> attributes = new HashMap<String,Object>();
	
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private ProtocolHandler<TcpSession,AuthControlPacket> protocolHandler;
	
	public AuthControlClientHandler(ProtocolHandler<TcpSession,AuthControlPacket> protocolHandler)
	{
		this.protocolHandler = protocolHandler;
	}
	
	public void setProtocolHandler(ProtocolHandler<TcpSession,AuthControlPacket> protocolHandler)
	{
		this.protocolHandler = protocolHandler;
	}
	
	@Override
	public void onSessionOpened(TcpSession session)
	{
		session.setAttribute(AuthControlClientSessionAttributes.CONTROLCLIENT_SESSION_STATE, AuthControlClientSessionState.CONNECTED);
		
		session.setAuthenticated(false);
		
		Message connected = new StandardMessage(session, MessageTopics.AUTHCONTROL_CLIENT_CONNECTED);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(connected);
		
		session.setAttribute(TcpSessionAttributes.LAST_PING_TX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PING_TX_TIME,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_TIME,(long)0);
		
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_TIME,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_TIME,Long.valueOf(0));

		WorldserverConfig config = (WorldserverConfig)AppRegistry.getInstance().retrieve(WorldserverConfig.class);
		
		GUID worldId = GUID.generate(config.getValue(WorldConfigKey.WORLD_ID));		
			
		AuthControlPacket packet = new AuthControlPacket(AuthControlOpcode.AUTH);
		packet.putLong(worldId.getMostSignificantBits());
		packet.putLong(worldId.getLeastSignificantBits());
		packet.putString(InetAddressUtil.getLocalhost().getHostName());
		packet.putString(config.getValue(WorldConfigKey.WORLD_NAME));
		packet.putLong(InetAddressUtil.hostname2Long(config.getValue(WorldConfigKey.WORLD_LISTENER_IP)));
		packet.putInt(config.getValueAs(Integer.class, WorldConfigKey.WORLD_LISTENER_PORT));
		packet.putString(config.getValue(WorldConfigKey.SECURITY_AUTH_KEY));
		packet.putInt(TyrannyConstants.AUTH_CONTROLSERVER_VERSION_MAJOR);
		packet.putInt(TyrannyConstants.AUTH_CONTROLSERVER_VERSION_MINOR);
		packet.putInt(TyrannyConstants.AUTH_CONTROLSERVER_VERSION_MAINT);
		packet.prepare();
		
		Message authfailed = new StandardMessage(session, MessageTopics.AUTHCONTROL_CLIENT_AUTHENTICATING);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(authfailed);
		
		session.setAttribute(AuthControlClientSessionAttributes.CONTROLCLIENT_SESSION_STATE, AuthControlClientSessionState.PENDING_AUTH);
		session.send(packet);
	}
	
	@Override
	public void onSessionClosed(TcpSession session)
	{
		LogHelper.info(this,"Lost connection to server: [{0}].", ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		synchronized(session)
		{
			session.setAttribute(AuthControlClientSessionAttributes.CONTROLCLIENT_SESSION_STATE, AuthControlClientSessionState.NULL);
		}

		Message disconnected = new StandardMessage(session, MessageTopics.AUTHCONTROL_CLIENT_DISCONNECTED);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(disconnected);		
	}
	
	@Override
	public void onConnectFailed(TcpSession session) 
	{
		Message connectionFailed = new StandardMessage(session, MessageTopics.AUTHCONTROL_CLIENT_CONNECTION_FAILED);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(connectionFailed);
	}

	@Override
	public void onSessionIdle(TcpSession session) 
	{
		Message idle = new StandardMessage(session, MessageTopics.AUTHCONTROL_CLIENT_SESSION_IDLE);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(idle);
	}
	
	@Override
	public void onMessageReceived(TcpSession session,AuthControlPacket message) 
	{
		lock.readLock().lock();
		try
		{
			if(protocolHandler != null)
			{
				protocolHandler.handle(session, message);
			}
		}
		finally
		{
			lock.readLock().unlock();
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
}