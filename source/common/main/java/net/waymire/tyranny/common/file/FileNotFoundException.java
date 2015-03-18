package net.waymire.tyranny.common.file;

public class FileNotFoundException extends FilesystemException {
	static final long serialVersionUID = 1L;
	
	public FileNotFoundException() {
		super();
	}
	
	public FileNotFoundException(String message) {
		super(message);
	}
	
	public FileNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public FileNotFoundException(String message,Throwable cause) {
		super(message,cause);
	}
}
