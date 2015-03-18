package net.waymire.tyranny.common.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class LogHelper {
	private static final Map<String,Logger> LOGGERS = new HashMap<>();
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private LogHelper() { }
	
	public static void setLogLevelSevere()
	{
		setLogLevel(Level.SEVERE);
	}
	
	public static void setLogLevelWarning()
	{
		setLogLevel(Level.WARNING);
	}
	
	public static void setLogLevelInfo()
	{
		setLogLevel(Level.INFO);
	}
	
	public static void setLogLevelDebug()
	{
		setLogLevel(Level.FINE);
	}
	
	public static void setLogLevelTrace()
	{
		setLogLevel(Level.FINEST);
	}
	
	public static void lock()
	{
		lock.writeLock().lock();
	}
	
	public static void unlock()
	{
		lock.writeLock().unlock();
	}

	public static void debug(final Object src,final String format,final Object...args)
	{
		log(src,Level.FINE,format,args);
	}
	
	public static void finer(final Object src,final String format,final Object...args)
	{
		log(src,Level.FINER,format,args);
	}
	
	public static void trace(final Object src,final String format,final Object...args)
	{
		log(src,Level.FINEST,format,args);
	}
	
	public static void info(final Object src,final String format,final Object...args)
	{
		log(src,Level.INFO,format,args);
	}
	
	public static void warning(final Object src,final String format,final Object...args)
	{
		log(src,Level.WARNING,format,args);
	}
	
	public static void severe(final Object src,final String format,final Object...args)
	{
		log(src,Level.SEVERE,format,args);
	}
	
	public static boolean isDebugEnabled(final Object src)
	{
		return getLogger(src).isLoggable(Level.FINE);
	}
	
	public static boolean isTraceEnabled(final Object src)
	{
		return getLogger(src).isLoggable(Level.FINEST);
	}
	
	private static Logger getLogger(final Object src)
	{
		Class<?> c = (src instanceof Class<?>) ? (Class<?>)src : src.getClass();
		return getLogger(c.getName());
	}
	
	private static Logger getLogger(String name)
	{
		if(!LOGGERS.containsKey(name))
		{
			LOGGERS.put(name, Logger.getLogger(name));
		}
		return LOGGERS.get(name);
	}
	
	private static void log(final Object src,final Level level,final String format,final Object...args)
	{
		lock.readLock().lock();
		try
		{
			Logger logger = getLogger(src);
			logger.log(level,format,args);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	private static void setLogLevel(Level level)
	{
		for(Logger logger : LOGGERS.values())
		{
			logger.setLevel(level);
		}
	}
}
