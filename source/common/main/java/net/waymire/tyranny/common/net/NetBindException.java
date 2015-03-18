package net.waymire.tyranny.common.net;

public class NetBindException extends NetworkException {
	static final long serialVersionUID = 1L;
	
	public NetBindException() {
		super();
	}
	
	public NetBindException(String message) {
		super(message);
	}
	
	public NetBindException(Throwable cause) {
		super(cause);
	}
	
	public NetBindException(String message,Throwable cause) {
		super(message,cause);
	}
}
