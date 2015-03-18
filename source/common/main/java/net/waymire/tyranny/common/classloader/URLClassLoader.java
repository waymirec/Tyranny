package net.waymire.tyranny.common.classloader;

import java.net.URL;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.waymire.tyranny.common.util.ExceptionUtil;

/**
 * This class loader is used to load classes and resources from a specified list
 * of URLs referring to both JAR files and directories.
 * 
 * This class loader will attempt to locate and load a class first and if it is
 * unable to locate the class the request will be forwarded on to its parent
 * class loader.
 * 
 */
public class URLClassLoader extends CustomClassLoader {
	private static final Logger LOGGER = Logger.getLogger(URLClassLoader.class
			.getName());
	private final List<URL> urlList = new ArrayList<URL>();

	public URLClassLoader(List<URL> urls, ClassLoader parent) {
		super(parent);
		if (urls != null) {
			this.urlList.addAll(urls);
		}
	}

	public URLClassLoader(List<URL> urls) {
		this(urls, URLClassLoader.class.getClassLoader());
	}

	public URLClassLoader(ClassLoader parent) {
		this(new ArrayList<URL>(), parent);
	}

	public URLClassLoader() {
		this(new ArrayList<URL>());
	}

	public void addURL(URL url) {
		if (url == null) {
			return;
		}
		this.urlList.add(url);
	}

	@Override
	public Object clone() {
		return new URLClassLoader(urlList, getParent());
	}

	@Override
	protected Class<?> loadClass(final String className, boolean resolve)
			throws ClassNotFoundException {
		Class<?> result = null;

		// has the class already been loaded?
		result = findLoadedClass(className);
		if (result != null) {
			return result;
		}

		// we will not attempt to load native java libraries ourselves
		if (!className.startsWith("java.")) {
			// try to find the class ourselves first, deviates from standard
			// ClassLoader routine
			result = findClass(className);
			if (result != null) {
				if (resolve) {
					resolveClass(result);
				}
				return result;
			}
		}

		// follow the parent chain to try to locate the class
		try {
			return findSystemClass(className);
		} catch (Exception e) {
		}
		return getParent().loadClass(className);
	}

	@Override
	protected Class<?> findClass(String className) {
		Class<?> result = null;
		for (URL url : urlList) {
			if (url.getProtocol().toUpperCase().equals("FILE")) {
				File file;
				try {
					file = new File(url.toURI());
					if (file.isFile()) {
						JarFile jar = new JarFile(file);
						JarEntry entry = jar.getJarEntry(className.replace('.','/') + ".class");
						jar.close();
						
						if (entry == null) {
							continue;
						}
						InputStream is = jar.getInputStream(entry);
						result = loadClassFromStream(className, is);
						return result;
					}
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, ExceptionUtil.getReason(e));
					LOGGER.log(Level.WARNING, ExceptionUtil.getStackTrace(e));
					return null;
				}

				if (file.isDirectory()) {
					final String target = className.toUpperCase() + ".CLASS";
					File[] classFiles = file.listFiles(new FilenameFilter() {
						public boolean accept(File file, String name) {
							return (target.endsWith(name.toUpperCase()));
						}
					});

					for (File classFile : classFiles) {
						try {
							InputStream is = new FileInputStream(classFile);
							result = loadClassFromStream(className, is);
							return result;
						} catch (IOException ioe) {
							LOGGER.log(Level.WARNING,
									ExceptionUtil.getReason(ioe));
							LOGGER.log(Level.WARNING,
									ExceptionUtil.getStackTrace(ioe));
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	protected URL findResource(final String resourceName) {
		try {
			URL url = super.findResource(resourceName);

			if (url != null) {
				return url;
			}
			for (URL u : urlList) {
				if (u.getProtocol().toUpperCase().equals("FILE")) {
					File file = new File(u.toURI());
					if (file.isFile()) {
						JarFile jar = new JarFile(file);
						JarEntry entry = jar.getJarEntry(resourceName);
						jar.close();
						
						if (entry == null) {
							continue;
						}
						String tmp = file.toURI().toURL().toString();
						return new URL("jar:" + tmp + "!/" + resourceName);
					}

					if (file.isDirectory()) {
						File f = new File(resourceName);
						if (f != null) {
							return f.toURI().toURL();
						}
					}
				}
			}
		} catch (Exception e) {
		}

		return null;
	}

	private Class<?> loadClassFromStream(String className, InputStream is)
			throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		int nextValue = is.read();
		while (-1 != nextValue) {
			byteStream.write(nextValue);
			nextValue = is.read();
		}

		byte[] bytes = byteStream.toByteArray();
		return defineClass(className, bytes, 0, bytes.length, null);
	}
}