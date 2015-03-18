package net.waymire.tyranny.common.net;

public class NetIoException extends NetworkException {
	static final long serialVersionUID = 1L;
	
	public NetIoException() {
		super();
	}
	
	public NetIoException(String message) {
		super(message);
	}
	
	public NetIoException(Throwable cause) {
		super(cause);
	}
	
	public NetIoException(String message,Throwable cause) {
		super(message,cause);
	}
}
