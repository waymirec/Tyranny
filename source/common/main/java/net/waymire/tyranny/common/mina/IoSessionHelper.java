package net.waymire.tyranny.common.mina;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.SocketSessionConfig;

import net.waymire.tyranny.common.net.IpConstants;
import net.waymire.tyranny.common.protocol.Packet;

/**
 * Static utility class providing helper operations on {@link IoSession} objects.
 * This class should not be instantiated.
 * 
 */
public final class IoSessionHelper 
{

	private IoSessionHelper() { }
	
	/**
	 * @param session	target {@link IoSession}
	 * @param time	idle time in seconds
	 * @see org.apache.mina.core.session.IoSessionConfig#setBothIdleTime(int idleTime)
	 */
	public static void setIdleTime(IoSession session,int time)
	{
		if(session == null)
		{
			return;
		}
		
		synchronized(session) 
		{
			if(session.isClosing() || !session.isConnected())
			{
				return;
			}
			session.getConfig().setIdleTime(IdleStatus.READER_IDLE,  time);
		}
	}
	
	/**
	 * @param session	target {@link IoSession}
	 * @return	idle time in seconds
	 * @see org.apache.mina.core.session.IoSessionConfig#getBothIdleTime()
	 */
	public static int getIdleTime(IoSession session)
	{
		if(session == null)
		{
			return 0;
		}
		
		synchronized(session) 
		{
			if(session.isClosing() || !session.isConnected())
			{
				
				return 0;
			}
			return session.getConfig().getIdleTime(IdleStatus.READER_IDLE);
		}
	}
	
	/** 
	 * @param session	target {@link IoSession}
	 * @param size	size of buffer in bytes
	 * @see org.apache.mina.transport.socket.SocketSessionConfig#setReceiveBufferSize(int receiveBufferSize)
	 */
	public static void setReceiveBufferSize(IoSession session,int size)
	{
		if(session == null)
		{
			return;
		}
		
		synchronized(session) {
			if(session.isClosing() || !session.isConnected())
			{
				return;
			}

			IoSessionConfig config = session.getConfig();
			if(SocketSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				((SocketSessionConfig)session.getConfig()).setReceiveBufferSize(size);
				return;
			}
			else if(DatagramSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				((DatagramSessionConfig)session.getConfig()).setReceiveBufferSize(size);
			}
			else
			{
				throw new IllegalArgumentException("Unsupported config type: " + config.getClass().getName());
			}
		}
	}
	
	/** 
	 * @param session target {@link IoSession}
	 * @return	size of the buffer in bytes
	 * @see org.apache.mina.transport.socket.SocketSessionConfig#getReceiveBufferSize()
	 */
	public static int getReceiveBufferSize(IoSession session)
	{
		if(session == null)
		{
			return 0;
		}
		
		synchronized(session)
		{
			if(session.isClosing() || !session.isConnected())
			{
				return 0;
			}
			
			IoSessionConfig config = session.getConfig();
			if(SocketSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				return ((SocketSessionConfig)session.getConfig()).getReceiveBufferSize();
			}
			else if(DatagramSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				return ((DatagramSessionConfig)session.getConfig()).getReceiveBufferSize();
			}
			else
			{
				throw new IllegalArgumentException("Unsupported config type: " + config.getClass().getName());
			}
		}
	}
	
	/** 
	 * @param session	target {@link IoSession}
	 * @param size	size of buffer in bytes
	 * @see org.apache.mina.transport.socket.SocketSessionConfig#setSendBufferSize(int sendBufferSize)
	 */
	public static void setSendBufferSize(IoSession session,int size)
	{
		if(session == null)
		{
			return;
		}
		
		synchronized(session) {
			IoSessionConfig config = session.getConfig();
			if(SocketSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				((SocketSessionConfig)session.getConfig()).setSendBufferSize(size);
				return;
			}
			else if(DatagramSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				((DatagramSessionConfig)session.getConfig()).setSendBufferSize(size);
				return;
			}
			else
			{
				throw new IllegalArgumentException("Unsupported config type: " + config.getClass().getName());
			}
		}
	}
	
	/** 
	 * @param session
	 * @return	size of the buffer in bytes
	 * @see org.apache.mina.transport.socket.SocketSessionConfig#getSendBufferSize()
	 */
	public static int getSendBufferSize(IoSession session)
	{
		if(session == null)
		{
			return 0;
		}
		
		synchronized(session)
		{
			IoSessionConfig config = session.getConfig();
			if(SocketSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				return ((SocketSessionConfig)session.getConfig()).getSendBufferSize();
			}
			else if(DatagramSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				return ((DatagramSessionConfig)session.getConfig()).getSendBufferSize();
			}
			else
			{
				throw new IllegalArgumentException("Unsupported config type: " + config.getClass().getName());
			}
		}
	}
	
	/**
	 * @param session	target {@link IoSession}
	 * @param yesno	boolean value to set
	 * @see org.apache.mina.transport.socket.SocketSessionConfig#setTcpNoDelay(boolean tcpNoDelay)
	 */
	public static void setTcpNoDelay(IoSession session,boolean yesno)
	{
		if(session == null)
		{
			return;
		}
		
		synchronized(session) 
		{
			IoSessionConfig config = session.getConfig();
			if(SocketSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				((SocketSessionConfig)session.getConfig()).setTcpNoDelay(yesno);
			}
			else
			{
				throw new IllegalArgumentException("Unsupported config type: " + config.getClass().getName());
			}
		}
	}
	
	/**
	 * @param session	target {@link IoSession}
	 * @return	boolean status of TcpNoDelay
	 * @see org.apache.mina.transport.socket.SocketSessionConfig#setTcpNoDelay(boolean tcpNoDelay)
	 */		
	public static boolean getTcpNoDelay(IoSession session)
	{
		if(session == null)
		{
			return false;
		}
		
		synchronized(session) 
		{
			IoSessionConfig config = session.getConfig();
			if(SocketSessionConfig.class.isAssignableFrom(config.getClass()))
			{
				return ((SocketSessionConfig)session.getConfig()).isTcpNoDelay();
			}
			else
			{
				throw new IllegalArgumentException("Unsupported config type: " + config.getClass().getName());
			}
		}
		
	}
	
	public static void setSessionId(IoSession session,Object sessionId)
	{
		IoSessionHelper.setAttribute(session,IpConstants.SESSION_ID,sessionId);
	}
	
	public static Object getSessionId(IoSession session)
	{
		return IoSessionHelper.getAttribute(session,IpConstants.SESSION_ID);
	}
	
	/** 
	 * @param session	target {@link IoSession}
	 * @param attrib	{@link IoSessionAttribute} key to set
	 * @param value	value of the {@link IoSessionAttribute}
	 * @see	org.apache.mina.core.session.IoSession#setAttribute(Object key, Object value)
	 */
	public static void setAttribute(IoSession session,String attributeKey,Object value)
	{
		if(session == null)
		{
			return;
		}
		
		synchronized(session)
		{
			session.setAttribute(attributeKey,value);
		}
	}
	
	/** 
	 * @param session	target {@link IoSession}
	 * @param attrib	{@link IoSessionAttribute} key
	 * @return	value of the attribute or <code>null</code> if not found
	 * @see	org.apache.mina.core.session.IoSession#getAttribute(Object key)
	 */
	public static Object getAttribute(IoSession session, String attributeKey)
	{
		if(session == null)
		{
			return null;
		}
		
		Object attribute;
		synchronized(session)
		{
			attribute = session.getAttribute(attributeKey);
		}
		return attribute;
	}

	/**
	 * @param session target {@link IoSession}
	 * @param attrib {@link IoSessionAttribute} key to remove
	 * @see org.apache.mina.core.session.IoSession#removeAttribute(Object)
	 */
	public static void removeAttribute(IoSession session,String attributeKey)
	{
		if(session == null)
		{
			return;
		}
		
		synchronized(session)
		{
			session.removeAttribute(attributeKey);
		}
	}
	
	public static void clearAttributes(IoSession session)
	{
		if(session == null)
		{
			return;
		}
		
		synchronized(session)
		{
			for(Object key : session.getAttributeKeys())
			{
				session.removeAttribute(key);
			}
		}
	}
	
	/**
	 * @param session	target {@link IoSession}
	 * @param packet	packet to send
	 * @see	org.apache.mina.core.session.IoSession#write(Object message)
	 */
	public static void writePacket(IoSession session,Packet<?> packet)
	{
		if(session == null)
		{
			return;
		}
		
		synchronized(session)
		{
			if(session.isClosing() || !session.isConnected())
			{
				return;
			}
			session.write(packet);
		}
	}
	
	/**
	 * @param session	target {@link IoSession}
	 * @return	{@link CloseFuture} indicating when the session actually closes
	 * @see	org.apache.mina.core.session.IoSession#close(boolean immediately) IoSession.close(boolean)
	 */
	public static CloseFuture closeSession(IoSession session)
	{
		if(session == null)
		{
			return null;
		}
		
		synchronized(session)
		{
			return session.close(true);
		}
	}	
}
