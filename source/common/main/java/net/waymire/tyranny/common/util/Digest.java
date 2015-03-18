package net.waymire.tyranny.common.util;

import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper around the {@link MessageDigest} class.
 * Checks for the existence of the SHA-1 provider at the time
 * that the class is loaded as to prevent exceptions and unnecessary
 * try/catch blocks.
 * 
 */
final public class Digest {
	private static final Logger LOGGER = Logger.getLogger(Digest.class.getName());
	
	{
		try {
			MessageDigest.getInstance( "SHA-1" );
			MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"Fatal Exception ({0}): {1}. Exiting...",new Object[]{e.getClass().getName(),e.getMessage()});
			System.exit(-1);
		}
	}
	
	private Digest() { }
	/**
	 * Sole constructor for this class. Returns an instance of 
	 * {@link MessageDigest} using the SHA-1 provider.
	 * 
	 * @return instance of {@link MessageDigest}
	 */
	public static MessageDigest sha1()
	{
		try 
		{
			return MessageDigest.getInstance( "SHA-1" );
		} 
		catch (Exception e) 
		{
			return null;
		}
	}
	
	public static MessageDigest md5()
	{
		try
		{
			return MessageDigest.getInstance("MD5");
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
