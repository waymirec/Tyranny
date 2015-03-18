package net.waymire.tyranny.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObjectRegistry {
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<Class<?>,Object> map = new HashMap<Class<?>,Object>();

	public <T> void put(T value)
	{
		if(value == null)
		{
			return;
		}

		lock.writeLock().lock();
		try
		{
			map.put(value.getClass(), value);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	public <T1,T2 extends T1> void put(Class<T1> clazz,T2 value)
	{
		if(value == null)
		{
			return;
		}

		lock.writeLock().lock();
		try
		{
			map.put(clazz, value);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> key)
	{
		lock.readLock().lock();
		try
		{
			return (T)map.get(key);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T remove(Class<T> key)
	{
		lock.writeLock().lock();
		try
		{
			return (T)map.remove(key);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public <T> boolean has(Class<T> key)
	{
		lock.readLock().lock();
		try
		{
			return map.containsKey(key);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> keys()
	{
		lock.readLock().lock();
		try
		{
			return new ArrayList<T>((Set<T>)map.keySet());
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> values()
	{
		lock.readLock().lock();
		try
		{
			return new ArrayList<T>((Collection<T>)map.values());
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	public int size()
	{
		lock.readLock().lock();
		try
		{
			return map.size();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
}
