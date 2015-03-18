package net.waymire.tyranny.common.file;

public class FilePermException extends FileException {
	private static final long serialVersionUID = -1l;
	public FilePermException(){ }
	public FilePermException(String message) { super(message); }
	public FilePermException(Throwable cause) { super(cause); }
}
