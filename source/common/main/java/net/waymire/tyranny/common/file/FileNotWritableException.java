package net.waymire.tyranny.common.file;

public class FileNotWritableException extends FilePermissionException {
	static final long serialVersionUID = 1L;

	public FileNotWritableException() {
		super();
	}
	
	public FileNotWritableException(String message) {
		super(message);
	}
	
	public FileNotWritableException(Throwable cause) {
		super(cause);
	}
	
	public FileNotWritableException(String message,Throwable cause) {
		super(message,cause);
	}
}
