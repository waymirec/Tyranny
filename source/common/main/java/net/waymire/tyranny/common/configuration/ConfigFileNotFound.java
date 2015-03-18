package net.waymire.tyranny.common.configuration;

public class ConfigFileNotFound extends ConfigFileException {
	static final long serialVersionUID = -1;
	
	public ConfigFileNotFound(String configFileName)
	{
		super(configFileName);
	}
}