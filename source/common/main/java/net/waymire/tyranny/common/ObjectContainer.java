package net.waymire.tyranny.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObjectContainer {
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<Object,Set<Object>> map = new HashMap<Object,Set<Object>>();

	public void put(Object key)
	{
		if(key == null)
		{
			return;
		}
		
		lock.readLock().lock();
		Set<Object> set = map.get(key);
		if(set == null)
		{
			lock.readLock().unlock();
			lock.writeLock().lock();
			try
			{
				set = new HashSet<Object>();
				map.put(key, set);
				lock.readLock().lock();
			}
			finally
			{
				lock.writeLock().unlock();
			}
		}
		lock.readLock().unlock();
	}
	
	public void put(Object key,Object value)
	{
		if((key == null) || (value == null))
		{
			return;
		}

		lock.writeLock().lock();
		try
		{
			Set<Object> set = map.get(key);
			if(set == null)
			{
				set = new HashSet<Object>();
				map.put(key, set);
			}
			set.add(value);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public Set<Object> get(Object key)
	{
		lock.readLock().lock();
		try
		{
			return map.get(key);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	public Set<Object> remove(Object key)
	{
		lock.writeLock().lock();
		try
		{
			return map.remove(key);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public boolean has(Object key)
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
	
	public List<Object> keys()
	{
		lock.readLock().lock();
		try
		{
			return new ArrayList<Object>((Set<Object>)map.keySet());
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	public Set<Object> values()
	{
		Set<Object> ret = new HashSet<Object>();
		lock.readLock().lock();
		try
		{
			for(Set<Object> set : map.values())
			{
				ret.addAll(set);
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public int size1()
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
	
	public int size2()
	{
		int ret = 0;
		lock.readLock().lock();
		try
		{
			for(Set<Object> set : map.values())
			{
				ret += set.size();
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
}
