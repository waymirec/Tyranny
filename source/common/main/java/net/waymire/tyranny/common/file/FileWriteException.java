package net.waymire.tyranny.common.file;

public class FileWriteException extends FilePermException {
	private static final long serialVersionUID = -1l;
	public FileWriteException(){ }
	public FileWriteException(String message) { super(message); }
	public FileWriteException(Throwable cause) { super(cause); }
}
