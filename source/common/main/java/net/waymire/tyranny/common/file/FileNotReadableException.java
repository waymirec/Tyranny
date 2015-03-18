package net.waymire.tyranny.common.file;

public class FileNotReadableException extends FilePermissionException {
	static final long serialVersionUID = 1L;
	
	public FileNotReadableException() {
		super();
	}
	
	public FileNotReadableException(String message) {
		super(message);
	}
	
	public FileNotReadableException(Throwable cause) {
		super(cause);
	}
	
	public FileNotReadableException(String message,Throwable cause) {
		super(message,cause);
	}
}
