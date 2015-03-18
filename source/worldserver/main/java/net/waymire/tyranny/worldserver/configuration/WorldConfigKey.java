package net.waymire.tyranny.worldserver.configuration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.waymire.tyranny.common.configuration.PropertyConfigKey;

public enum WorldConfigKey implements PropertyConfigKey
{
	WORLD_ID("tyranny.worldserver.world.id"),
	WORLD_NAME("tyranny.worldserver.world.name"),
	WORLD_LISTENER_IP("tyranny.worldserver.world.listener.ip"),
	WORLD_LISTENER_PORT("tyranny.worldserver.world.listener.port"),
	
	CONTROL_SERVER_IP("tyranny.worldserver.control.server.ip"),
	CONTROL_SERVER_PORT("tyranny.worldserver.control.server.port"),
	
	SECURITY_AUTH_KEY("tyranny.worldserver.security.auth.key"),
	SECURITY_AUTH_MAX_ATTEMPTS("tyranny.worldserver.security.auth.attempts.max"),
	;
	
	private static final Map<String,WorldConfigKey> lookup =  new HashMap<String,WorldConfigKey>();
	
	static
	{
		for(WorldConfigKey key : EnumSet.allOf(WorldConfigKey.class))
		{
			lookup.put(key.value(),key);
		}
	}

	private String value;

	private WorldConfigKey(String value)
	{
		this.value = value;
	}
	
	public String value()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
}
