package net.waymire.tyranny.common.protocol;

/**
 * Signals that an exception has occurred due to a packet being too large.
 * 
 * @author Chris Waymire
 *
 */
public class PacketTooBigException extends PacketSizeException {
	final static long serialVersionUID = -1;

	/**
	 * Constructs a PacketTooBigException with <code>message</code> as its error detail message.
	 * @param message	error detail message
	 */	
	public PacketTooBigException(String message)
	{
		super(message);
	}
	/**
	 * Constructs a PacketTooBigException with <code>causedby</code> as its trigger.
	 * @param causedby	triggering exception
	 */	
	public PacketTooBigException(Exception causedBy)
	{
		super(causedBy);
	}
	/**
	 * Constructs a PacketTooBigException with <code>message</code> as its error detail message and
	 * <code>causedby</code> as its trigger.
	 * @param message	error detail message
	 * @param causedby	triggering exception
	 */	
	public PacketTooBigException(String message,Exception causedby)
	{
		super(message,causedby);
	}		
}
