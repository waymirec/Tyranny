package net.waymire.tyranny.common.dynload;
import java.io.File;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.waymire.tyranny.common.SystemConstants;
import net.waymire.tyranny.common.annotation.AnnotationScanResult;
import net.waymire.tyranny.common.annotation.AnnotationScanner;
import net.waymire.tyranny.common.annotation.ByteCodeAnnotationScanner;
import net.waymire.tyranny.common.annotation.AnnotationScanResult.ResultType;
import net.waymire.tyranny.common.file.FileNotFoundException;
import net.waymire.tyranny.common.file.FileNotReadableException;
import net.waymire.tyranny.common.file.InvalidFileTypeException;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.util.ClassUtil;
import net.waymire.tyranny.common.util.ExceptionUtil;
import net.waymire.tyranny.common.util.FileList;

/**
 * The Autoloader is a singleton based class that is responsible for discovering classes 
 * with the Autoload annotation. Those classes are then instantiated and, if needed,
 * initialized and started.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public class Autoloader
{
	private static Autoloader INSTANCE = null;
	
	private final List<Object> loadedObjects = new ArrayList<Object>();
	
	public static Autoloader getInstance()
	{
		if(INSTANCE == null)
		{
			synchronized(Autoloader.class)
			{
				if(INSTANCE == null)
				{
					INSTANCE = new Autoloader();
				}
			}
		}
		return INSTANCE;
	}
	
	/**
	 * Default constructor for this class
	 */
	private Autoloader()
	{
	}
	
	/**
	 * Return a collection of the class instances loaded by this instance of Autoloader.
	 * 
	 * @return	Collection of loaded class instances.
	 */
	public List<Object> getLoadedObjects()
	{
		return Collections.unmodifiableList(loadedObjects);
	}
	
	/**
	 * Discover and instantiate instances of all classes that are flagged for autoloading.
	 */
	public void load(String rootPath)
	{
		File dir = new File(rootPath);
		
		if(!dir.exists())
		{
			throw new FileNotFoundException(dir.getAbsolutePath());
		}
		
		if(!dir.isDirectory())
		{
			throw new InvalidFileTypeException(dir.getAbsolutePath());
		}
		
		if(!dir.canRead())
		{
			throw new FileNotReadableException(dir.getAbsolutePath());
		}
		
		AnnotationScanner<File> scanner = new ByteCodeAnnotationScanner();
		List<File> files = FileList.findBySuffix(SystemConstants.JAR_FILE_EXTENSION,dir);
		
		List<AnnotationScanResult> results = scanner.scan(files, Autoload.class.getName());
		if(results != null)
		{
			Map<Integer,List<String>> map = new HashMap<>();
			for(AnnotationScanResult result : results)
			{
				if(result.getType().equals(ResultType.CLASS))
				{
					Integer priority = (Integer)result.getMember("priority");
				
					if(LogHelper.isDebugEnabled(this))
					{
						LogHelper.debug(this, "Found Autoload Class [{0}] With Priority [{1}]", result.getClassName(), priority);
					}
					
					if(priority ==  null)
					{
						priority = Integer.MAX_VALUE;
					}
					
					if(!map.containsKey(priority))
					{
						map.put(priority, new ArrayList<String>());
					}
					
					map.get(priority).add(result.getClassName());
				}
			}
			
			final List<Integer> keys = new ArrayList<Integer>(map.keySet());
			
			Collections.sort(keys);
			for(Integer priority : keys)
			{
				List<String> classnames = map.get(priority);
				for (String classname : classnames)
				{
					try
					{
						Class<?> clazz = Class.forName(classname);
						Method autoload = ClassUtil.getMethod(clazz, "autoload");
						Object obj = null;
						if(autoload != null)
						{
							if(LogHelper.isDebugEnabled(this))
							{
								LogHelper.debug(this, "Instantiating Class [{0}] Via The 'autoload' Method.", classname);
							}
							obj = autoload.invoke(clazz);
						}
						else
						{
							if(LogHelper.isDebugEnabled(this))
							{
								LogHelper.debug(this, "Instantiating Class [{0}] Via The Constructor.", classname);
							}
							obj = clazz.newInstance();
						}
						
						loadedObjects.add(obj);
					}
					catch(ReflectiveOperationException reflectiveOpEx)
					{
						LogHelper.warning(this, "Error Autoloading Class [{0}]: {1}.", classname, ExceptionUtil.getStackTrace(reflectiveOpEx));
					}
				}
			}
			
			for(Object obj : loadedObjects)
			{
				if(obj instanceof AutoInitializable)
				{
					if(LogHelper.isDebugEnabled(this))
					{
						LogHelper.debug(this, "Initializing Autoloaded Class [{0}].", obj.getClass().getName());
					}
					((AutoInitializable)obj).autoInitialize();
				}
			}
			
			for(Object obj : loadedObjects)
			{
				if(obj instanceof AutoStartable)
				{
					if(LogHelper.isDebugEnabled(this))
					{
						LogHelper.debug(this, "Starting Autoloaded Class [{0}].", obj.getClass().getName());
					}
					((AutoStartable)obj).autoStart();
				}
			}
						
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "Done Scanning For Autoload Classes.");
			}
		}
		else
		{
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "No Autoloader Classes Found.");
			}
		}
	}
		
	/**
	 * Stop all of the loaded class instances.
	 */
	public void unload()
	{
		if(LogHelper.isDebugEnabled(this))
		{
			LogHelper.debug(this, "Unloading Autoload Classes.");
		}

		for(int i=loadedObjects.size()-1; i >= 0; i--)
		{
			Object obj = loadedObjects.get(i);
			if(obj instanceof AutoStartable)
			{
				if(LogHelper.isDebugEnabled(this))
				{
					LogHelper.debug(this, "Stopping Autoloaded Class [{0}].", obj.getClass().getName());
				}
				((AutoStartable)obj).autoStop();
			}
		}
		
		if(LogHelper.isDebugEnabled(this))
		{
			LogHelper.debug(this, "Done Unloading Autoload Classes.");
		}
	}
}
