package net.waymire.tyranny.common.delegate;

public class DelegateInvocationException extends RuntimeException {
	static final long serialVersionUID = 1L;
	
	public DelegateInvocationException(String message) {
		super(message);
	}

	public DelegateInvocationException(Exception causedBy) {
		super(causedBy);
	}

	public DelegateInvocationException(String message, Exception causedby) {
		super(message, causedby);
	}
}
