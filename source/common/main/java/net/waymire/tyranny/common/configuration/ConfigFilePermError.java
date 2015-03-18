package net.waymire.tyranny.common.configuration;

public class ConfigFilePermError extends ConfigFileException {
	static final long serialVersionUID = -1;
	
	public ConfigFilePermError(String configFileName)
	{
		super(configFileName);
	}
}
