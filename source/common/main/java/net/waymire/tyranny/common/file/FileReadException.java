package net.waymire.tyranny.common.file;

public class FileReadException extends FilePermException {
	private static final long serialVersionUID = -1l;
	public FileReadException(){ }
	public FileReadException(String message) { super(message); }
	public FileReadException(Throwable cause) { super(cause); }
}
