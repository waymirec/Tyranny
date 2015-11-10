package net.waymire.tyranny.client.configuration;

import java.util.EnumSet;
import java.util.Properties;

import net.waymire.tyranny.client.ClientEnvironment;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.Environment;
import net.waymire.tyranny.common.TyrannyConstants;
import net.waymire.tyranny.common.util.PropertiesUtil;

public class ConfigurationFactory 
{
	public static ClientConfig getClientConfiguration()
	{
		ClientConfig config = new ClientConfig();

		ClientEnvironment env = AppRegistry.getInstance().retrieve(ClientEnvironment.class);
		StringBuilder sb = new StringBuilder(env.getFullConfigPath()).append(Environment.getFileSeparator());
		String configFilename = new StringBuilder(sb).append(TyrannyConstants.AUTH_SERVER_CONFIG).toString();
		Properties props = PropertiesUtil.load(configFilename);
		for(ClientConfigKey key : EnumSet.allOf(ClientConfigKey.class))
		{
			config.setValue(key,  props.getProperty(key.value()));
		}
		return config;
	}
	
	private ConfigurationFactory() { }
}
