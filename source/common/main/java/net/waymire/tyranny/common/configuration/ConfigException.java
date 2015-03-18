package net.waymire.tyranny.common.configuration;

public class ConfigException extends RuntimeException {
	static final long serialVersionUID = -1;
	
	public ConfigException(String message)
	{
		super(message);
	}
	
	public ConfigException(Throwable t)
	{
		super(t);
	}
}