package net.waymire.tyranny.common.protocol;

/**
 * Signals that a malformed packet was received.
 * 
 * @author Chris Waymire
 *
 */
public class MalformedPacketException extends ProtocolException {
	final static long serialVersionUID = -1;
	
	/**
	 * Constructs a MalformedPacketException with no error detail message.
	 */
	public MalformedPacketException()
	{
		super();
	}
	
	/**
	 * Constructs a MalformedPacketException with <code>message</code> as its error detail message.
	 * @param message	error detail message
	 */
	public MalformedPacketException(String message)
	{
		super(message);
	}
	/**
	 * Constructs a MalformedPacketException with <code>causedby</code> as its trigger.
	 * @param causedby	triggering exception
	 */	
	public MalformedPacketException(Exception causedBy)
	{
		super(causedBy);
	}
	/**
	 * Constructs a MalformedPacketException with <code>message</code> as its error detail message and
	 * <code>causedby</code> as its trigger.
	 * @param message	error detail message
	 * @param causedby	triggering exception
	 */
	public MalformedPacketException(String message,Exception causedby)
	{
		super(message,causedby);
	}
}