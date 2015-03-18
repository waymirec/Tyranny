package net.waymire.tyranny.common.configuration;

public class ConfigPathException extends ConfigException {
	static final long serialVersionUID = -1;
	
	public ConfigPathException(String message)
	{
		super(message);
	}
	
	public ConfigPathException(Throwable t)
	{
		super(t);
	}
}
