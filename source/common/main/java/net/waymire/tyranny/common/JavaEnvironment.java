package net.waymire.tyranny.common;

import java.util.Properties;

/**
 * Static utility class allowing for easier access to the Java system properties.
 * 
 */
public final class JavaEnvironment {
	private static final Properties m_props = System.getProperties();
	
	private JavaEnvironment() { }
	
	/**
	 * Checks whether the system property identified by <code>key</code> exists.
	 * @param key property to look for.
	 * @return <code>true</code> if the property exists, else <code>false</code>.
	 * 
	 */
	public static boolean isPropertySet(String key)
	{
		return m_props.containsKey(key);
	}
	
	/**
	 * Retrieve the system property identified by <code>key</code.
	 * @param key	property to retrieve.
	 * @return	<code>String</code> value of the property if it exists, else <code>null</null>
	 */
	public static String getProperty(String key)
	{
		return m_props.getProperty(key);
	}
	
	/**
	 * Set the value of the property identified by <code>key</code>.
	 * @param key	property to set
	 * @param value	value to set the property to.
	 */
	public static void setProperty(String key,String value)
	{
		m_props.setProperty(key,value);
	}	
}
