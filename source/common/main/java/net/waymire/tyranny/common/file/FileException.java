package net.waymire.tyranny.common.file;

public class FileException extends Exception {
	private static final long serialVersionUID = -1l;
	public FileException(){ }
	public FileException(String message) { super(message); }
	public FileException(Throwable cause) { super(cause); }
}
