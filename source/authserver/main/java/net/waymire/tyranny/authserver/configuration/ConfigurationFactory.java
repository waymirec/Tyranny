package net.waymire.tyranny.authserver.configuration;

import java.util.EnumSet;
import java.util.Properties;

import net.waymire.tyranny.authserver.AuthserverEnvironment;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.Environment;
import net.waymire.tyranny.common.TyrannyConstants;
import net.waymire.tyranny.common.util.PropertiesUtil;

public class ConfigurationFactory 
{
	
	public static AuthserverConfig getAuthserverConfiguration()
	{	
		AuthserverConfig config = new AuthserverConfig();

		AuthserverEnvironment env = AppRegistry.getInstance().retrieve(AuthserverEnvironment.class);
		StringBuilder sb = new StringBuilder(env.getFullConfigPath()).append(Environment.getFileSeparator());
		String configFilename = new StringBuilder(sb).append(TyrannyConstants.AUTH_SERVER_CONFIG).toString();
		Properties props = PropertiesUtil.load(configFilename);
		for(AuthConfigKey key : EnumSet.allOf(AuthConfigKey.class))
		{
			config.setValue(key,  props.getProperty(key.value()));
		}
		return config;
	}
	
	private ConfigurationFactory() { }
}
