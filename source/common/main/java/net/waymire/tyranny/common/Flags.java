package net.waymire.tyranny.common;

import java.io.Serializable;

/**
 * Represents a Bitwise value with methods to set and clear 
 * individual flags.
 * 
 * @param <T>	the specific subclass of {@link Flag} to use.
 */
public class Flags implements Cloneable,Serializable {
	static final long serialVersionUID = -1L;
	
	public static final Flags EMPTY = new Flags();
	
	private long value;
	
	/**
	 * Default constructor for this class.
	 * Initializes this object with a default value of 0.
	 */
	public Flags()
	{
		this.value = 0;
	}	
	/**
	 * Constructor to initialize this object with a preset value.
	 * 
	 * @param flags	value to initialize this object to.
	 */
	public Flags(long flags)
	{
		this.value = flags;
	}	
	/**
	 * Initialize this object with a preset value.
	 * 
	 * @param flags value to initialize this object to.	
	 */
	public void setFlags(long flags)
	{
		this.value = flags;
	}		
	/**
	 * Initialize this object based on the value of another Flags object.
	 * 
	 * @param flags Flags object to initialize this object from.
	 */
	public void setFlags(Flags flags)
	{
		this.value = flags.longValue();
	}	
	/**
	 * Set a specific flag within this object. The argument passed
	 * must be of the class specified when this object was defined.
	 * 
	 * @param flag specific {@link Flag} to set.
	 */
	public <T extends Flag> void setFlag(T flag)
	{
		this.value = this.value | flag.longValue();
	}	
	/**
	 * Clear a specific flag within this object. The argument passed
	 * must be of the same class specified when this object was defined.
	 * 
	 * @param flag specific {@link Flag} to clear.
	 */
	public <T extends Flag> void clearFlag(T flag)
	{
		this.value = this.value & ~flag.longValue();
	}	
	/**
	 * Check whether a specific flag within this object is set. The
	 * argument passed must be of the same class specified when this object was 
	 * defined.
	 * 
	 * @param flag	specific {@link Flag} to look for.
	 * @return true if flag is set. false otherwise.
	 */
	public <T extends Flag> boolean hasFlag(T flag)
	{
		return ((this.value & flag.longValue()) == flag.longValue());
	}	
	/**
	 * Returns the <code>long</code> value of the flags.
	 * @return	value of the flags as a <code>long</code>
	 */
	public long longValue()
	{
		return (long)this.value;
	}	
	/**
	 * Returns the <code>int</code> value of the flags.
	 * @return	value of the flags as an <code>int</code>
	 */
	public int intValue()
	{
		return (int)this.value;
	}
	/**
	 * Returns the <code>short</code> value of the flags.
	 * @return	value of the flags as a <code>short</code>
	 */
	public short shortValue()
	{
		return (short)this.value;
	}
	/**
	 * Returns the <code>byte</code> value of the flags.
	 * @return	value of the flags as a <code>byte</code>
	 */
	public byte byteValue()
	{
		return (byte)this.value;
	}
	/**
	 * Sets the <code>long</code> value of this object.
	 * @param value	value to assign this object.
	 */
	public void setValue(long value)
	{
		this.value = value;
	}	
	/**
	 * Returns a clone of this object.
	 * @return	a clone of this object.
	 */
	public Flags clone()
	{
		return new Flags(this.longValue());
	}
}
