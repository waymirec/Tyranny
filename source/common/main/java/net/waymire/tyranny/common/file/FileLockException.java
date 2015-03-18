package net.waymire.tyranny.common.file;

public class FileLockException extends FileException {
	private static final long serialVersionUID = -1l;
	public FileLockException(){ }
	public FileLockException(String message) { super(message); }
	public FileLockException(Throwable cause) { super(cause); }
}
