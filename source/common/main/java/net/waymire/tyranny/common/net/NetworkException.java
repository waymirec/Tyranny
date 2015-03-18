package net.waymire.tyranny.common.net;

public class NetworkException extends RuntimeException {
static final long serialVersionUID = 1L;
	
	public NetworkException() {
		super();
	}
	
	public NetworkException(String message) {
		super(message);
	}
	
	public NetworkException(Throwable cause) {
		super(cause);
	}
	
	public NetworkException(String message,Throwable cause) {
		super(message,cause);
	}
}
