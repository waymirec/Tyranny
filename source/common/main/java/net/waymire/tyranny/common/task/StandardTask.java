package net.waymire.tyranny.common.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;

import net.waymire.tyranny.common.GUID;

public class StandardTask implements Task
{
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final GUID guid;
	private final AtomicBoolean disabled = new AtomicBoolean(false);
	private final Map<String,Object> attributes = new HashMap<>();
	
	private TaskFuture future = null;
	private String name = null;
	private IterationType iterationType = null;
	private long period = -1;
	private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
	
	public StandardTask()
	{
		this.guid = GUID.generate();
		this.name = guid.toString();
	}
	
	@Override
	public GUID getId()
	{
		return guid;
	}
	
	@Override
	public void setFuture(TaskFuture future)
	{
		this.future = future;
	}
	
	@Override
	public TaskFuture getFuture()
	{
		return future;
	}
	
	@Override
	public void setName(String name)
	{
		lock.writeLock().lock();
		this.name = name;
		lock.writeLock().unlock();
	}
	
	@Override
	public String getName()
	{
		lock.readLock().lock();
		try
		{
			return !StringUtils.isEmpty(name) ? name : guid.toString();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public IterationType getIterationType()
	{
		lock.readLock().lock();
		try
		{
			return iterationType;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void setIterationType(IterationType type)
	{
		lock.writeLock().lock();
		try
		{
			this.iterationType = type;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public long getPeriod()
	{
		lock.readLock().lock();
		try
		{
			return period;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void setPeriod(long period)
	{
		lock.writeLock().lock();
		try
		{
			this.period = period;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public TimeUnit getTimeUnit()
	{
		lock.readLock().lock();
		try
		{
			return timeUnit;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void setTimeUnit(TimeUnit timeUnit)
	{
		lock.writeLock().lock();
		try
		{
			this.timeUnit = timeUnit;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean hasAttribute(String key)
	{
		lock.readLock().lock();
		try
		{
			return attributes.containsKey(key);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void setAttribute(String key, Object value)
	{
		lock.writeLock().lock();
		try
		{
			attributes.put(key, value);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public Object getAttribute(String key)
	{
		lock.readLock().lock();
		try
		{
			return attributes.get(key);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void run()
	{
		execute();
	}
	
	@Override
	public void disable()
	{
		lock.writeLock().lock();
		disabled.set(true);
		lock.writeLock().unlock();
	}
	
	@Override
	public boolean isDisabled()
	{
		lock.readLock().lock();
		try
		{
			return disabled.get();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	public void execute()
	{
	}
	
	@Override
	public void setup()
	{
	}
	
	@Override
	public void teardown()
	{
		
	}
	
	@Override
	public ReadWriteLock getLock()
	{
		return lock;
	}
}
