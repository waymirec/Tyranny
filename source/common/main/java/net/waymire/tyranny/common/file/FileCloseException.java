package net.waymire.tyranny.common.file;

public class FileCloseException extends FileException {
	private static final long serialVersionUID = -1l;
	public FileCloseException(){ }
	public FileCloseException(String message) { super(message); }
	public FileCloseException(Throwable cause) { super(cause); }

}
