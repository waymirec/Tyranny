package net.waymire.tyranny.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObjectManager<K,V> {
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<K,V> map = new HashMap<K,V>();
	
	public void put(K key, V value)
	{
		if((key == null) || (value == null))
		{
			return;
		}
		
		lock.writeLock().lock();
		try
		{
			map.put(key,value);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public V get(K key)
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
	
	public V remove(K key)
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
	
	public boolean has(K key)
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
	
	public List<K> keys()
	{
		lock.readLock().lock();
		try
		{
			return new ArrayList<K>(map.keySet());
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	public List<V> values()
	{
		lock.readLock().lock();
		try
		{
			return new ArrayList<V>(map.values());
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
	
	public void clear()
	{
		map.clear();
	}
}
