package net.waymire.tyranny.worldserver.configuration;

import java.util.EnumSet;
import java.util.Properties;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.Environment;
import net.waymire.tyranny.common.TyrannyConstants;
import net.waymire.tyranny.common.util.PropertiesUtil;
import net.waymire.tyranny.worldserver.WorldserverEnvironment;

public class ConfigurationFactory 
{
	
	public static WorldserverConfig getWorldserverConfiguration()
	{	
		WorldserverConfig config = new WorldserverConfig();

		WorldserverEnvironment env = AppRegistry.getInstance().retrieve(WorldserverEnvironment.class);
		StringBuilder sb = new StringBuilder(env.getFullConfigPath()).append(Environment.getFileSeparator());
		String configFilename = new StringBuilder(sb).append(TyrannyConstants.WORLD_SERVER_CONFIG).toString();
		Properties props = PropertiesUtil.load(configFilename);
		for(WorldConfigKey key : EnumSet.allOf(WorldConfigKey.class))
		{
			config.setValue(key,  props.getProperty(key.value()));
		}
		return config;
	}
	
	private ConfigurationFactory() { }
}
