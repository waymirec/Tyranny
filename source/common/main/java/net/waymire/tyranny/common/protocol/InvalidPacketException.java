package net.waymire.tyranny.common.protocol;

/**
 * Signals that an invalid or unexpected packet was received.
 * 
 * @author Chris Waymire
 *
 */
public class InvalidPacketException extends ProtocolException {
	final static long serialVersionUID = -1;
	
	/**
	 * Constructs a InvalidPacketException with <code>message</code> as its error detail message.
	 * @param message	error detail message
	 */
	public InvalidPacketException(String message)
	{
		super(message);
	}
	/**
	 * Constructs a InvalidPacketException with <code>causedby</code> as its trigger.
	 * @param causedby	triggering exception
	 */
	public InvalidPacketException(Exception causedBy)
	{
		super(causedBy);
	}
	/**
	 * Constructs a InvalidPacketException with <code>message</code> as its error detail message and
	 * <code>causedby</code> as its trigger.
	 * @param message	error detail message
	 * @param causedby	triggering exception
	 */
	public InvalidPacketException(String message,Exception causedby)
	{
		super(message,causedby);
	}
}
