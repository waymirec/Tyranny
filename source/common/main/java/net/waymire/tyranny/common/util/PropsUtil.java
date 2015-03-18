package net.waymire.tyranny.common.util;

import java.util.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class PropsUtil
{

	private PropsUtil()
	{
	}

	/**
	 * Load a properties file from the classpath
	 * 
	 * @param propsName
	 * @return Properties
	 * @throws Exception
	 */
	public static Properties load(String propsName) throws FileNotFoundException, URISyntaxException, IOException
	{
		if (propsName == null)
		{
			throw new IllegalArgumentException("null input");
		}
		URL url = ClassLoader.getSystemResource(propsName);
		if (url == null)
		{
			throw new FileNotFoundException();
		}
		return load(new File(url.toURI()));
	}

	/**
	 * Load a Properties File
	 * 
	 * @param propsFile
	 * @return Properties
	 * @throws IOException
	 */
	public static Properties load(File propsFile) throws FileNotFoundException, IOException
	{
		if (propsFile == null)
		{
			throw new IllegalArgumentException("null input");
		}

		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(propsFile);
		props.load(fis);
		fis.close();
		return props;
	}

	public static void save(Properties props, File propsFile) throws FileNotFoundException, URISyntaxException, IOException
	{
		save(props, propsFile.getAbsolutePath());
	}

	public static void save(Properties props, String propsFileName) throws FileNotFoundException, IOException
	{
		FileOutputStream fos = new FileOutputStream(new File(propsFileName));
		props.store(fos, null);
		fos.close();
	}
}
