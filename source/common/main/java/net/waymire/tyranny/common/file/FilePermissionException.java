package net.waymire.tyranny.common.file;

public class FilePermissionException extends FilesystemException {
	static final long serialVersionUID = 1L;
	
	public FilePermissionException() {
		super();
	}
	
	public FilePermissionException(String message) {
		super(message);
	}
	
	public FilePermissionException(Throwable cause) {
		super(cause);
	}
	
	public FilePermissionException(String message,Throwable cause) {
		super(message,cause);
	}
}
