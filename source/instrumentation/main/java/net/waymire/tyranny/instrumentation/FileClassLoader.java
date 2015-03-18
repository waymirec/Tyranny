package net.waymire.tyranny.instrumentation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class loader is used to load classes and resources from a specified list
 * of Files referring to both JAR files and directories.
 * 
 * This class loader will attempt to locate and load a class first and if it is
 * unable to locate the class the request will be forwarded on to its parent
 * class loader.
 */
public class FileClassLoader extends CustomClassLoader 
{
	private final List<File> fileList = new ArrayList<File>();

	public FileClassLoader(List<File> files, ClassLoader parent) 
	{
		super(parent);
		
		if (files != null) 
		{
			this.fileList.addAll(files);
		}
	}

	public FileClassLoader(List<File> files) 
	{
		this(files, FileClassLoader.class.getClassLoader());
	}

	public FileClassLoader(ClassLoader parent) {
		this(new ArrayList<File>(), parent);
	}

	public FileClassLoader() {
		this(new ArrayList<File>());
	}

	public void addFile(File file) {
		if (file == null) {
			return;
		}

		if (!file.exists() || !file.canRead()) {
			throw new IllegalArgumentException();
		}

		if (file.isFile() && !file.getName().toUpperCase().endsWith(".JAR")) {
			throw new IllegalArgumentException();
		}

		this.fileList.add(file);
	}

	@Override
	public Object clone() {
		return new FileClassLoader(fileList, getParent());
	}

	@Override
	protected Class<?> loadClass(final String className, boolean resolve) throws ClassNotFoundException 
	{
		Class<?> result = null;

		// has the class already been loaded?
		result = findLoadedClass(className);
		if (result != null) 
		{
			return result;
		}

		// we will not attempt to load native java libraries ourselves
		if (!className.startsWith("java.")) 
		{
			// try to find the class ourselves first, deviates from standard
			// ClassLoader routine
			result = findClass(className);
			if (result != null) 
			{
				if (resolve) 
				{
					resolveClass(result);
				}
				return result;
			}
		}

		// follow the parent chain to try to locate the class
		try 
		{
			return findSystemClass(className);
		} 
		catch (Exception e) 
		{
		}
		
		return getParent().loadClass(className);
	}

	@Override
	protected Class<?> findClass(String className) 
	{
		Class<?> result = null;
		for (File file : fileList) {
			if (file.isFile()) 
			{
				try 
				{
					JarFile jar = new JarFile(file);
					JarEntry entry = jar.getJarEntry(className.replace('.', '/') + ".class");
					jar.close();
					
					if (entry == null) 
					{
						continue;
					}
					
					InputStream is = jar.getInputStream(entry);
					result = loadClassFromStream(className, is);
					return result;
				} 
				catch (IOException ioe) 
				{
					ioe.printStackTrace();
				}
			}

			if (file.isDirectory()) 
			{
				final String target = className.toUpperCase() + ".CLASS";
				File[] classFiles = file.listFiles(new FilenameFilter() {
					public boolean accept(File file, String name) {
						return (target.endsWith(name.toUpperCase()));
					}
				});

				for (File classFile : classFiles) 
				{
					try 
					{
						InputStream is = new FileInputStream(classFile);
						result = loadClassFromStream(className, is);
						return result;
					} 
					catch (IOException ioe) 
					{
						ioe.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	@Override
	protected URL findResource(final String resourceName) 
	{
		URL url = super.findResource(resourceName);

		if (url != null) 
		{
			return url;
		}

		for (File file : fileList) 
		{
			if (file.isFile()) 
			{
				try
				{
					JarFile jar = new JarFile(file);
					JarEntry entry = jar.getJarEntry(resourceName);
					jar.close();
					if (entry == null) {
					continue;
					}
					String tmp = file.toURI().toURL().toString();
					return new URL("jar:" + tmp + "!/" + resourceName);
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
					return null;
				}
			}

			if (file.isDirectory()) 
			{
				File f = new File(resourceName);
				if (f != null) 
				{
					try
					{
						return f.toURI().toURL();
					}
					catch(MalformedURLException mue)
					{
						mue.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	private Class<?> loadClassFromStream(String className, InputStream is) throws IOException 
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		int nextValue = is.read();
		while (-1 != nextValue) 
		{
			byteStream.write(nextValue);
			nextValue = is.read();
		}

		byte[] bytes = byteStream.toByteArray();
		return defineClass(className, bytes, 0, bytes.length, null);
	}
}
