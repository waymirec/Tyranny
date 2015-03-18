package net.waymire.tyranny.common;

import java.util.HashMap;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Registry is an object container allowing for the storing of Objects keyed by either
 * a String or by a Class.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public class Registry
{
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<String,Object> map = new HashMap<String,Object>();
	private final ObjectRegistry objreg = new ObjectRegistry();
	
	public void lock()
	{
		lock.writeLock().lock();
	}
	
	public void unlock()
	{
		lock.writeLock().unlock();
	}
	
	/**
	 * Store the specified value in the registry under the specified String key.
	 * 
	 * @param key			the registry key to store
	 * @param value			the registry value to store
	 */
	public void register(String key,Object value)
	{
		lock.writeLock().lock();
		try
		{
			map.put(key, value);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * Store <code>value</code> in the registry under <code>key</code>.
	 * The <code>value</code> object must be an instance of a 
	 * class that is assignable to <code>key</code> 
	 * 
	 * @param key			the registry key to store
	 * @param value			the registry value to store
	 */
	public <T1,T2 extends T1> void register(Class<T1> key, T2 value)
	{
		lock.writeLock().lock();
		try
		{
			objreg.put(key,value);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	public void unregister(String key)
	{
		lock.writeLock().lock();
		try
		{
			map.remove(key);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public <T1> void unregister(Class<T1> key)
	{
		lock.writeLock().lock();
		try
		{
			objreg.remove(key);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Returns the Object stored in the registry under the
	 * specified key. This method will return a generic
	 * java Object.
	 * 
	 * @param key			the registry key to retrieve
	 * @return				The registry Object stored under
	 * 						the specified key.
	 */
	public Object retrieve(String key)
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
	
	/**
	 * Returns the Object stored in the registry under the
	 * specified key. This method will return the object
	 * stored casted to the provided type.
	 * 
	 * @param key			the registry key to retrieve
	 * @return				The registry Object stored under
	 * 						the specified key.
	 */
	public <T> T retrieve(Class<T> key)
	{
		lock.readLock().lock();
		try
		{
			return objreg.get(key);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Checks the registry for the existence of the provided key.
	 * 
	 * @param key			The key to check for
	 * @return				<code>true</code> if the registry contains
	 * 						the key.
	 * 						<code>false</code> otherwise.
	 */
	public boolean contains(String key)
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
	
	/**
	 * Checks the registry for the existence of the provided key.
	 * 
	 * @param key			The key to check for
	 * @return				<code>true</code> if the registry contains
	 * 						the key.
	 * 						<code>false</code> otherwise.
	 */
	public boolean contains(Class<?> key)
	{
		lock.readLock().lock();
		try
		{
			return objreg.has(key);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
}
