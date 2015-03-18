package net.waymire.tyranny.common.protocol;

/**
 * Signals that an exception has occurred regarding the size of a packet.
 * 
 * @author Chris Waymire
 *
 */
public class PacketSizeException extends ProtocolException {
	final static long serialVersionUID = -1;

	/**
	 * Constructs a PacketSizeException with <code>message</code> as its error
	 * detail message.
	 * 
	 * @param message
	 *            error detail message
	 */
	public PacketSizeException(String message) {
		super(message);
	}

	/**
	 * Constructs a PacketSizeException with <code>causedby</code> as its
	 * trigger.
	 * 
	 * @param causedby
	 *            triggering exception
	 */
	public PacketSizeException(Exception causedBy) {
		super(causedBy);
	}

	/**
	 * Constructs a PacketSizeException with <code>message</code> as its error
	 * detail message and <code>causedby</code> as its trigger.
	 * 
	 * @param message
	 *            error detail message
	 * @param causedby
	 *            triggering exception
	 */
	public PacketSizeException(String message, Exception causedby) {
		super(message, causedby);
	}
}
