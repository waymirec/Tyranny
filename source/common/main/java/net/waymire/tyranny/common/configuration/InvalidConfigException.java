package net.waymire.tyranny.common.configuration;

public class InvalidConfigException extends ConfigException {
	static final long serialVersionUID = -1;
	
	public InvalidConfigException(String err)
	{
		super(err);
	}
	
	public InvalidConfigException(Throwable t)
	{
		super(t);
	}
}
