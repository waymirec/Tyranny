package net.waymire.tyranny.common.classloader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CachingClassLoader extends CustomClassLoader
{
	private final Map<String,Class<?>> cache = new HashMap<String,Class<?>>();
	
	public CachingClassLoader()
	{
		this(CachingClassLoader.class.getClassLoader());
	}
	
	public CachingClassLoader(ClassLoader parent)
	{
		super(parent);
	}
	
	protected Map<String,Class<?>> getCache()
	{
		return Collections.unmodifiableMap(cache);
	}
	
	protected boolean isCached(String className)
	{
		return cache.containsKey(className);
	}
	
	protected void addCached(String className,Class<?> klass)
	{
		cache.put(className,klass);
	}
	
	protected Class<?> getCached(String className)
	{
		return cache.get(className);
	}
	
	@Override
	protected Class<?> loadClass(String name,boolean resolve) throws ClassNotFoundException
	{
		Class<?> result = cache.get(name);
		if(result != null)
		{
			return result;
		}
		
		result = getParent().loadClass(name);
		
		if(result != null)
		{
			cache.put(name,result);
		}
		
		return result;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		throw new ClassNotFoundException(name);
	}
}
