package net.waymire.tyranny.common.file;

public class InvalidFileTypeException extends FilesystemException {
	static final long serialVersionUID = 1L;
	
	public InvalidFileTypeException() {
		super();
	}
	
	public InvalidFileTypeException(String message) {
		super(message);
	}
	
	public InvalidFileTypeException(Throwable cause) {
		super(cause);
	}
	
	public InvalidFileTypeException(String message,Throwable cause) {
		super(message,cause);
	}
}
