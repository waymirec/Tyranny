package net.waymire.tyranny.authserver.configuration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.waymire.tyranny.common.configuration.PropertyConfigKey;

public enum AuthConfigKey implements PropertyConfigKey
{
	LOGIN_LISTENER_IP("tyranny.authserver.login.listener.ip"),
	LOGIN_LISTENER_PORT("tyranny.authserver.login.listener.port"),
	
	CONTROL_LISTENER_IP("tyranny.authserver.control.listener.ip"),
	CONTROL_LISTENER_PORT("tyranny.authserver.control.listener.port"),
	
	SECURITY_AUTH_KEY("tyranny.authserver.security.auth.key"),
	SECURITY_AUTH_MAX_ATTEMPTS("tyranny.authserver.security.auth.attempts.max"),
	
	DB_HOST("tyranny.authserver.db.host"),
	DB_PORT("tyranny.authserver.db.port"),
	DB_USERNAME("tyranny.authserver.db.username"),
	DB_PASSWORD("tyranny.authserver.db.password"),
	DB_DBNAME("tyranny.authserver.db.databaseName")
	;
	
	private static final Map<String,AuthConfigKey> lookup =  new HashMap<String,AuthConfigKey>();
	
	static
	{
		for(AuthConfigKey key : EnumSet.allOf(AuthConfigKey.class))
		{
			lookup.put(key.value(),key);
		}
	}

	private String value;

	private AuthConfigKey(String value)
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
