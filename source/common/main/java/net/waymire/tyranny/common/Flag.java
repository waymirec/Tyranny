package net.waymire.tyranny.common;

/**
 * Base interface for representing a bitwise flag.
 * 
 */
public interface Flag {
	/**
	 * Returns the <code>long</code> value of the flag.
	 * @return	value of the flag as a <code>long</code>
	 */
	public long longValue();
	/**
	 * Returns the <code>int</code> value of the flag.
	 * @return	value of the flag as a <code>int</code>
	 */	
	public int intValue();	
	/**
	 * Returns the <code>short</code> value of the flag.
	 * @return	value of the flag as a <code>short</code>
	 */
	public short shortValue();
	/**
	 * Returns the <code>byte</code> value of the flag.
	 * @return	value of the flag as a <code>byte</code>
	 */
	public byte byteValue();	
}
