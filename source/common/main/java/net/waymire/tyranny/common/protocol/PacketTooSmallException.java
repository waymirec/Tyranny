package net.waymire.tyranny.common.protocol;

/**
 * Signals that an exception has occurred due to a packet being too small.
 * 
 * @author Chris Waymire
 *
 */
public class PacketTooSmallException extends PacketSizeException {
	final static long serialVersionUID = -1;

	/**
	 * Constructs a PacketTooSmallException with <code>message</code> as its
	 * error detail message.
	 * 
	 * @param message
	 *            error detail message
	 */
	public PacketTooSmallException(String message) {
		super(message);
	}

	/**
	 * Constructs a PacketTooSmallException with <code>causedby</code> as its
	 * trigger.
	 * 
	 * @param causedby
	 *            triggering exception
	 */
	public PacketTooSmallException(Exception causedBy) {
		super(causedBy);
	}

	/**
	 * Constructs a PacketTooSmallException with <code>message</code> as its
	 * error detail message and <code>causedby</code> as its trigger.
	 * 
	 * @param message
	 *            error detail message
	 * @param causedby
	 *            triggering exception
	 */
	public PacketTooSmallException(String message, Exception causedby) {
		super(message, causedby);
	}
}
