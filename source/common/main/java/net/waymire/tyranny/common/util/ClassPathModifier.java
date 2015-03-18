package net.waymire.tyranny.common.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final public class ClassPathModifier {
	private static final Logger LOGGER = Logger.getLogger(ClassPathModifier.class.getName());
	private static final Class<?>[] parameters = new Class<?>[]{URL.class};

	private ClassPathModifier() { }
	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}//end method
	 
	public static void addFile(URLClassLoader loader,String s) throws IOException {
		File f = new File(s);
		addFile(loader,f);
	}
	
	public static void addFile(File f) throws IOException {		
		addURL(f.toURI().toURL());
	}//end method
	 
	public static void addFile(URLClassLoader loader, File f) throws IOException {
		addURL(loader,f.toURI().toURL());
	}
	
	public static void addURL(URLClassLoader loader, URL u) throws IOException {
		List<URL> list = new ArrayList<URL>();
		list.add(u);
		addURL(loader,list);
	}
	
	public static void addURL(URL u) throws IOException {
		List<URL> list = new ArrayList<URL>();
		list.add(u);
		addURL(list);
	}//end method
	
	public static void addURL(List<URL> list) throws IOException {
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		addURL(sysloader,list);
	}//end method
	
	public static void addURL(URLClassLoader loader,List<URL> list) throws IOException {
		try
		{
			Method method = URLClassLoader.class.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(loader,list.toArray());
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE,ExceptionUtil.getReason(e));
			LOGGER.log(Level.SEVERE,ExceptionUtil.getStackTrace(e));
			throw new IOException("Error, could not add URL to URLClassLoader " + loader);
		}
	}
}
