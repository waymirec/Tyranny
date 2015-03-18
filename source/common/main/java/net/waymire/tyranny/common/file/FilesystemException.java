package net.waymire.tyranny.common.file;

public class FilesystemException extends RuntimeException {
	static final long serialVersionUID = 1L;
	
	public FilesystemException() {
		super();
	}
	
	public FilesystemException(String message) {
		super(message);
	}
	
	public FilesystemException(Throwable cause) {
		super(cause);
	}
	
	public FilesystemException(String message,Throwable cause) {
		super(message,cause);
	}
}
