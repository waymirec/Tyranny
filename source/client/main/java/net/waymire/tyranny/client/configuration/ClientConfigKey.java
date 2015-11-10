package net.waymire.tyranny.client.configuration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.waymire.tyranny.common.configuration.PropertyConfigKey;

public enum ClientConfigKey implements PropertyConfigKey
{
	INPUT_ACTION_SHOOT("input.action.shoot"),
	
	INPUT_MOVE_FORWARD("input.move.forward"),
	INPUT_MOVE_BACKWARD("input.move.backward"),
	INPUT_MOVE_LEFT("input.move.left"),
	INPUT_MOVE_RIGHT("input.move.right"),
	INPUT_MOVE_ROT_LEFT("input.move.rot_left"),
	INPUT_MOVE_ROT_RIGHT("input.move.rot_right"),
	INPUT_MOVE_JUMP("input.move.jump"),
	INPUT_MOVE_RUN_TOGGLE("input.move.run_toggle"),
	INPUT_MOVE_STRAFE("input.move.strafe"),

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
