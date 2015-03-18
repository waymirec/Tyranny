package net.waymire.tyranny.common.delegate;

public class DelegateCreationException extends RuntimeException {
	static final long serialVersionUID = 1L;
	
	public DelegateCreationException()
	{
		super();
	}
	
	public DelegateCreationException(Throwable cause)
	{
		super(cause);
	}
	
	public DelegateCreationException(String message)
	{
		super(message);
	}
	
	public DelegateCreationException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
