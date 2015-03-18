package net.waymire.tyranny.common.protocol;

public class ProtocolException extends RuntimeException {
	static final long serialVersionUID = 1L;
	
	public ProtocolException() {
		super();
	}
	
	public ProtocolException(String message) {
		super(message);
	}
	
	public ProtocolException(Throwable cause) {
		super(cause);
	}
	
	public ProtocolException(String message,Throwable cause) {
		super(message,cause);
	}
}
