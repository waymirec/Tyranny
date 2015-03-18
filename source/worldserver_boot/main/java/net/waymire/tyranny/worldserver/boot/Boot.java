package net.waymire.tyranny.worldserver.boot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public final class Boot 
{
	private static final String APP_REGISTRY_CLASSNAME = "net.waymire.tyranny.common.AppRegistry";
	private static final String WORLDSERVER_ENVIRONMENT_CLASSNAME = "net.waymire.tyranny.worldserver.WorldserverEnvironment";
	private static final String WORLDSERVER_MASTER_CLASSNAME = "net.waymire.tyranny.worldserver.WorldserverMaster";
	private static final String WORLDSERVER_MASTER_METHODNAME = "main";
	private static final String CACHING_CLASSLOADER_CLASSNAME = "net.waymire.tyranny.common.classloader.CachingClassLoader";
	private static final String CLASSUTIL_CLASSNAME = "net.waymire.tyranny.common.util.ClassUtil";
	private static final String FILE_CLASSLOADER_CLASSNAME = "net.waymire.tyranny.common.classloader.FileClassLoader";
	private static final String FILTER_TYPE_CLASSNAME = "net.waymire.tyranny.common.classloader.FilteringClassLoader$FilterType";
	private static final String FILTERING_CLASSLOADER_CLASSNAME = "net.waymire.tyranny.common.classloader.FilteringClassLoader";
	private static final String FILE_LIST_CLASSNAME = "net.waymire.tyranny.common.util.FileList";
	
	private final File self;
	private final File parent;
	private final File root;
	
	private ClassLoader systemClassLoader = null;
	private ClassLoader bootClassLoader = null;
	
	public static void main(String[] args) 
	{
		new Boot();
	}
	
	private Boot()
	{
		self = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		parent = self.getParentFile();
		root = parent.getParentFile();
		
		initSystemClassLoader();
		initBootClassLoader();
		initWorldserverEnvironment();
		launchMaster();
	}

	private void initSystemClassLoader()
	{
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		
		try
		{
			final String pattern = String.format("glob:%s/**/*.jar", root.getAbsolutePath());
			final PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pattern);
			final List<URL> urls = new ArrayList<URL>();
			
			Files.walkFileTree(Paths.get(root.getAbsolutePath()), new SimpleFileVisitor<Path>() {
			        @Override
			        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException 
			        {
			        	if (matcher.matches(file)) {
			        		URL url = new File(file.toString()).toURI().toURL();
		        			urls.add(url);
			            }
			            return FileVisitResult.CONTINUE;
			        }

			        @Override
			        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			            return FileVisitResult.CONTINUE;
			        }
			});
			systemClassLoader = new URLClassLoader(urls.toArray(new URL[0]), contextClassLoader);
			Thread.currentThread().setContextClassLoader(systemClassLoader);
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}
	
	private void initBootClassLoader()
	{
		try
		{
			Class<?> fileClassLoaderClass = systemClassLoader.loadClass(FILE_CLASSLOADER_CLASSNAME);
			Constructor<?> fileClassLoaderConstructor = fileClassLoaderClass.getConstructor(new Class[]{ClassLoader.class});
			ClassLoader fileLoader = (ClassLoader)fileClassLoaderConstructor.newInstance(systemClassLoader);
		
			Object environment = loadWorldserverEnvironment(systemClassLoader);
			File lib = new File((String)environment.getClass().getDeclaredMethod("getFullLibraryPath").invoke(environment));
			File proc = new File((String)environment.getClass().getDeclaredMethod("getFullProcessorPath").invoke(environment));

			Class<?> fileListClass = systemClassLoader.loadClass(FILE_LIST_CLASSNAME);
			Method fileListMethod = fileListClass.getMethod("findBySuffix",new Class[]{String.class,File[].class});
			@SuppressWarnings("unchecked")
			List<File> jars = (List<File>)fileListMethod.invoke(null, ".jar", new File[]{lib, proc});
			
			for(File jar : jars)
			{
				fileClassLoaderClass.getMethod("addFile", new Class[]{File.class}).invoke(fileLoader, jar);
			}
		
			Class<?> filteringClassLoaderClass = systemClassLoader.loadClass(FILTERING_CLASSLOADER_CLASSNAME);
			Constructor<?> filteringClassLoaderConstructor = filteringClassLoaderClass.getConstructor(new Class[]{ClassLoader.class});
			ClassLoader filteringLoader = (ClassLoader)filteringClassLoaderConstructor.newInstance(fileLoader);
			
			@SuppressWarnings("unchecked")
			Class<Enum> filterTypeClass = (Class<Enum>)systemClassLoader.loadClass(FILTER_TYPE_CLASSNAME);
			
			@SuppressWarnings("unchecked")
			Object filterType = Enum.valueOf(filterTypeClass, "DENY");			
			filteringClassLoaderClass.getMethod("setFilterType", filterTypeClass).invoke(filteringLoader, filterType);
		
			Class<?> cachingClassLoaderClass = systemClassLoader.loadClass(CACHING_CLASSLOADER_CLASSNAME);
			Constructor<?> cachingClassLoaderConstructor = cachingClassLoaderClass.getConstructor(new Class[]{ClassLoader.class});
			ClassLoader cachingLoader = (ClassLoader)cachingClassLoaderConstructor.newInstance(filteringLoader);
			
			bootClassLoader = cachingLoader;
			Thread.currentThread().setContextClassLoader(bootClassLoader);
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}
	
	private Object loadWorldserverEnvironment(ClassLoader loader)
	{
		Object ret = null;
		
		try
		{
			Class<?> c = loader.loadClass(WORLDSERVER_ENVIRONMENT_CLASSNAME);
			Class<?>[] argsClass = new Class<?>[] { String.class };
			Object[] args = new Object[] { root.getAbsolutePath() };
			Constructor<?> constructor = c.getConstructor(argsClass);
			ret = constructor.newInstance(args);
		}
		catch(Exception e)
		{
			handleException(e);
		}
		
		return ret;
	}
	
	private void initWorldserverEnvironment()
	{
		try
		{
			Object environment = loadWorldserverEnvironment(Thread.currentThread().getContextClassLoader());
			File conf = new File((String)environment.getClass().getDeclaredMethod("getFullConfigPath").invoke(environment));
			
			Class<?> classUtilClass = Thread.currentThread().getContextClassLoader().loadClass(CLASSUTIL_CLASSNAME);
			Method getMethod = classUtilClass.getMethod("getMethod", Class.class, String.class, Class[].class);
		
			Class<?> worldserverEnvClass = Thread.currentThread().getContextClassLoader().loadClass(WORLDSERVER_ENVIRONMENT_CLASSNAME);
			Class<?> appRegistryClass = Thread.currentThread().getContextClassLoader().loadClass(APP_REGISTRY_CLASSNAME);
	
			Method appRegistryGetInstanceMethod = (Method)getMethod.invoke(null, appRegistryClass, "getInstance", new Class[0]);
			Method appRegistryRegisterMethod = (Method)getMethod.invoke(null, appRegistryClass, "register", new Class[]{Class.class, Object.class});
		
			Object appRegistryInstance = appRegistryGetInstanceMethod.invoke(null);
			appRegistryRegisterMethod.invoke(appRegistryInstance, worldserverEnvClass, environment);

			System.setProperty("java.util.logging.config.file", conf.getAbsolutePath());
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}
	
	private void launchMaster()
	{
		try
		{
			Class<?> masterClass = Thread.currentThread().getContextClassLoader().loadClass(WORLDSERVER_MASTER_CLASSNAME);
			Method main = masterClass.getMethod(WORLDSERVER_MASTER_METHODNAME,new Class<?>[] { (new String[0]).getClass() });
			main.invoke(null, (Object)new String[]{});
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}
	
	private void handleException(Exception e)
	{
		System.out.printf("Failed to boot...\n");
		e.printStackTrace();
		System.exit(0);
	}
}