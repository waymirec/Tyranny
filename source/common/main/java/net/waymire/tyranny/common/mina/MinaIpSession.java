package net.waymire.tyranny.common.mina;

import java.lang.ref.WeakReference;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.session.IoSession;

import net.waymire.tyranny.common.net.IpSession;
import net.waymire.tyranny.common.protocol.Packet;

public abstract class MinaIpSession implements IpSession
{
	protected final WeakReference<IoSession> ioSession;
	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	protected boolean authenticated = false;
	
	public MinaIpSession(IoSession session)
	{
		this.ioSession = new WeakReference<IoSession>(session);
	}
	
	public IoSession getIoSession()
	{
		lock.readLock().lock();
		try
		{
			return ioSession.get();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean isConnected() 
	{
		lock.readLock().lock();
		try
		{
			if(ioSession != null && ioSession.get() != null)
			{
				return ioSession.get().isConnected();
			}
			return false;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void close()
	{
		lock.writeLock().lock();
		try
		{
			if(isValid())
			{
				if(!ioSession.get().isClosing())
				{
					ioSession.get().close(true);
				}
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean isClosing()
	{
		lock.readLock().lock();
		try
		{
			if(isValid())
			{
				return ioSession.get().isClosing();
			}
			return false;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public long getId()
	{
		lock.readLock().lock();
		try
		{
			if(isValid())
			{
				return ioSession.get().getId();
			}
			return -1;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public SocketAddress getRemoteAddress()
	{
		lock.readLock().lock();
		try
		{
			if(isValid())
			{
				return ioSession.get().getRemoteAddress();
			}
			return null;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public void setAuthenticated(boolean authenticated)
	{
		lock.writeLock().lock();
		try
		{
			this.authenticated = authenticated;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean getAuthenticated()
	{
		lock.readLock().lock();
		try
		{
			return authenticated;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void setIdleTime(int value)
	{
		lock.writeLock().lock();
		try
		{
			IoSessionHelper.setIdleTime(ioSession.get(), value);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public int getIdleTime()
	{
		lock.readLock().lock();
		try
		{
			return IoSessionHelper.getIdleTime(ioSession.get());
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void setAttribute(Object key, Object value) 
	{
		lock.writeLock().lock();
		try
		{
			if(isValid())
			{
				ioSession.get().setAttribute(key,value);
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public Object getAttribute(Object key) 
	{
		lock.readLock().lock();
		try
		{
			if(isValid())
			{
				return ioSession.get().getAttribute(key);
			}
			return null;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public void clearAttribute(Object key)
	{
		this.setAttribute(key, null);
	}
	
	@Override
	public void clearAttributes()
	{
		lock.writeLock().lock();
		try
		{
			if(isValid())
			{
				Iterator<Object> it = ioSession.get().getAttributeKeys().iterator();
				while(it.hasNext())
				{
					it.next();
					it.remove();
				}	
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void send(Packet<?> message) 
	{
		lock.writeLock().lock();
		try
		{
			if(isValid())
			{
				if(ioSession.get().isConnected() && !ioSession.get().isClosing())
				{
					ioSession.get().write(message);
					return;
				}
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean isValid()
	{
		lock.readLock().lock();
		try
		{
			return ioSession != null && ioSession.get() != null;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int hashCode()
	{
		lock.readLock().lock();
		try
		{
			if(isValid())
			{
				return new Long(ioSession.get().getId()).hashCode();
			}
			return -1;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean equals(Object other)
	{
		lock.readLock().lock();
		try
		{
			if((other != null) && (other instanceof MinaTcpSession))
			{
				if(isValid())
				{
					return (ioSession.get().getId() == ((MinaTcpSession)other).getId());
				}
			}
			return false;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}