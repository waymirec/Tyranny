package net.waymire.tyranny.common;

import java.util.Map;

/**
 * Static utility class allowing for easier access to the Java environment variables.
 * 
 */
public final class SystemEnvironment {
	private static final Map<String, String> m_env = System.getenv();
		
	private SystemEnvironment() { }
	
	/**
	 * Checks whether the environment variable identified by <code>key</code> exists.
	 * @param key property to look for.
	 * @return <code>true</code> if the property exists, else <code>false</code>.
	 * 
	 */	
	public static boolean isPropertySet(String key)
	{
		return m_env.containsKey(key);
	}

	/**
	 * Retrieve the environment variable identified by <code>key</code.
	 * @param key	property to retrieve.
	 * @return	<value of the environment variable if it exists, else <code>null</null>
	 */	
	public static String getProperty(String key)
	{
		return m_env.get(key);
	}

	/**
	 * Set the value of the property identified by <code>key</code>.
	 * @param key	property to set
	 * @param value	value to set the environemtn variable to.
	 */	
	public static void setProperty(String key,String value)
	{
		m_env.put(key,value);
	}	
}
