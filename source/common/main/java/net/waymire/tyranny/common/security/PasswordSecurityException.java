package net.waymire.tyranny.common.security;

public class PasswordSecurityException extends RuntimeException {
static final long serialVersionUID = 1L;
	
	public PasswordSecurityException() {
		super();
	}
	
	public PasswordSecurityException(String message) {
		super(message);
	}
	
	public PasswordSecurityException(Throwable cause) {
		super(cause);
	}
	
	public PasswordSecurityException(String message,Throwable cause) {
		super(message,cause);
	}
}
