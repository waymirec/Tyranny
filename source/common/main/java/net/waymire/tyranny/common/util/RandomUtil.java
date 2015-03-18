package net.waymire.tyranny.common.util;

import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper around the {@link SecureRandom} class.
 * Checks for the existence of the SHA1PRNG algorithm at the time
 * that the class is loaded as to prevent exceptions and unnecessary
 * try/catch blocks.
 * 
 * @author Chris Waymire
 *
 */
final public class RandomUtil {
	private static final Logger LOGGER = Logger.getLogger(RandomUtil.class.getName());
	
	{
		try {
			SecureRandom.getInstance( "SHA1PRNG" );
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"Fatal Exception ({0}): {1}. Exiting...",new Object[]{e.getClass().getName(),e.getMessage()});
			System.exit(-1);
		}
	}

	private RandomUtil() { }
	
	/**
	 * Sole constructor for this class. Returns an instance of 
	 * {@link SecureRandom} using the SHA1PRNG algorithm.
	 * 
	 * @return instance of {@link SecureRandom}
	 */	
	public static SecureRandom sha1()
	{
		try {
			return SecureRandom.getInstance("SHA1PRNG");
		} catch (Exception e) {
			return null;
		}
	}
}
