package net.waymire.tyranny.common.util;

import java.util.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;

final public class PropertiesUtil
{

	private PropertiesUtil()
	{
	}

	/**
	 * Load a properties file from the classpath
	 * 
	 * @param propsName
	 * @return Properties
	 * @throws Exception
	 */
	public static Properties load(String propsName)
	{
		if(!StringUtils.isEmpty(propsName))
		{
			File propsFile = new File(propsName);
			if(propsFile.exists() && propsFile.canRead())
			{
				return load(propsFile);
			}
		}
		return null;
	}

	/**
	 * Load a Properties File
	 * 
	 * @param propsFile
	 * @return Properties
	 * @throws IOException
	 */
	public static Properties load(File propsFile)
	{
		if(propsFile != null)
		{
			Properties props = new Properties();
			try
			{
				FileInputStream fis = new FileInputStream(propsFile);
				try
				{
					props.load(fis);
				}
				finally
				{
					fis.close();
				}
			}
			catch(IOException ioException)
			{
				return null;
			}
			return props;
		}
		return null;
	}

	public static void save(Properties props, File propsFile) throws FileNotFoundException, URISyntaxException, IOException
	{
		save(props, propsFile.getAbsolutePath());
	}

	public static void save(Properties props, String propsFileName) throws FileNotFoundException, IOException
	{
		FileOutputStream fos = new FileOutputStream(new File(propsFileName));
		try
		{
			props.store(fos, null);
		}
		finally
		{
			fos.close();
		}
	}
}
