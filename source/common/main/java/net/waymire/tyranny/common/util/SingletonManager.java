package net.waymire.tyranny.common.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SingletonManager {
    private static final Map<Class<?>,Object> INSTANCES = new HashMap<Class<?>,Object>();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private SingletonManager() { }

    @SuppressWarnings("unchecked")
	public static <T> T get(Class<T> type)
    {
        lock.readLock().lock();
        if(!INSTANCES.containsKey(type))
        {
            lock.readLock().unlock();
            lock.writeLock().lock();
            try
            {
                if(!INSTANCES.containsKey(type))
                {
                    try
                    {
                        Constructor<T> constructor = type.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        T instance = constructor.newInstance();
                        INSTANCES.put(type,instance);
                    }
                    catch (Exception exception)
                    {
                        throw new RuntimeException(exception);
                    }
                }
            }
            finally
            {
                lock.writeLock().unlock();
                lock.readLock().lock();
            }
        }
        lock.readLock().unlock();
        return (T)INSTANCES.get(type);
    }
}
