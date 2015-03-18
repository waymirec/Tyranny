package net.waymire.tyranny.common.net;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.util.ExceptionUtil;

abstract public class BaseIpServerHandler<S extends IpSession, T extends Packet<? extends Opcode>> implements IpServerHandler<S, T>
{
	private final Map<String,Object> attributes = new HashMap<String,Object>();
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private ProtocolHandler<S,T> protocolHandler;
	
	public BaseIpServerHandler(ProtocolHandler<S,T> protocolHandler)
	{
		this.protocolHandler = protocolHandler;
	}
	
	public BaseIpServerHandler()
	{
		this(null);
	}
	
	@Override
	public void setProtocolHandler(ProtocolHandler<S,T> protocolHandler)
	{
		lock.writeLock().lock();
		try { this.protocolHandler = protocolHandler; } finally { lock.writeLock().unlock(); }
	}
	
	@Override
	public void onSessionOpened(S session) 
	{
		long now = System.currentTimeMillis();
		
		session.setAttribute(IpSessionAttributes.GUID, GUID.generate());
		session.setAuthenticated(false);
			
		session.setAttribute(IpSessionAttributes.LAST_PING_TX_SEQ,Long.valueOf(0));
		session.setAttribute(IpSessionAttributes.LAST_PING_TX_TIME,now);
		session.setAttribute(IpSessionAttributes.LAST_PONG_RX_SEQ,Long.valueOf(0));
		session.setAttribute(IpSessionAttributes.LAST_PONG_RX_TIME,now);
			
		session.setAttribute(IpSessionAttributes.LAST_PING_RX_SEQ,Long.valueOf(0));
		session.setAttribute(IpSessionAttributes.LAST_PING_RX_TIME,now);
		session.setAttribute(IpSessionAttributes.LAST_PONG_TX_SEQ,Long.valueOf(0));
		session.setAttribute(IpSessionAttributes.LAST_PONG_TX_TIME,now);
	}

	@Override
	public void onSessionClosed(S session) 
	{
		session.clearAttributes();
	}

	@Override
	public void onSessionIdle(S session) 
	{

	}

	@Override
	public void onMessageReceived(S session, T message) 
	{
		lock.readLock().lock();
		try
		{
			if(protocolHandler != null)
			{
				protocolHandler.handle(session, message);
			} else
			{
				LogHelper.warning(this, "Packet Received But No Protocol Handler Defined. Class = [{0}], Opcode = [{1}].", this.getClass().getName(), message.opcode());
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public void onException(S session, Throwable t) 
	{
		LogHelper.severe(this,"EXCEPTION [{0}]: {1}",new Object[]{session,ExceptionUtil.getStackTrace(t)});
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
	
	protected ReentrantReadWriteLock getLock()
	{
		return lock;
	}
	
	protected ProtocolHandler<S,T> getProtocolHandler()
	{
		return protocolHandler;
	}
}
