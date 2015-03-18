package net.waymire.tyranny.client.configuration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.waymire.tyranny.common.configuration.PropertyConfigKey;

public enum ClientConfigKey implements PropertyConfigKey
{
	;
	
	private static final Map<String,ClientConfigKey> lookup =  new HashMap<String,ClientConfigKey>();
	
	static
	{
		for(ClientConfigKey key : EnumSet.allOf(ClientConfigKey.class))
		{
			lookup.put(key.value(),key);
		}
	}

	private String value;

	private ClientConfigKey(String value)
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
