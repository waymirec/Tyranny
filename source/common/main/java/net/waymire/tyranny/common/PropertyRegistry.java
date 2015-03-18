package net.waymire.tyranny.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PropertyRegistry<K> {
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<K,Object> map = new HashMap<K,Object>();
	
	public <V> void set(K key, V value)
	{
		if(value == null)
		{
			return;
		}
		
		lock.writeLock().lock();
		map.put(key, value);
		lock.writeLock().unlock();
	}
	
	@SuppressWarnings("unchecked")
	public <V> V get(K key)
	{
		lock.readLock().lock();
		try
		{
			return (V)map.get(key);
		}
		finally
		{
			lock.readLock().unlock();
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
}
