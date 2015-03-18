package net.waymire.tyranny.common;

import java.util.UUID;

import net.waymire.tyranny.common.util.Digest;

/**
 * This class generates an array of bytes for use as a CHAP challenge.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public class CHAPChallengeGenerator
{
	/**
	 * Generate an array of bytes for use as a CHAP challenge.
	 * 
	 * @return			array of bytes.
	 */
	public static byte[] generate()
	{
		return Digest.sha1().digest(UUID.randomUUID().toString().getBytes());
	}
}
