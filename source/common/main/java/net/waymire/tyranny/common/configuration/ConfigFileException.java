package net.waymire.tyranny.common.configuration;

public class ConfigFileException extends ConfigException {
	static final long serialVersionUID = -1;
	
	public ConfigFileException(String configFileName)
	{
		super(configFileName);
	}
	
	public ConfigFileException(Throwable t)
	{
		super(t);
	}
}