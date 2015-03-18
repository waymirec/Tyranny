package net.waymire.tyranny.common.protocol;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum WorldserverOpcode implements Opcode {
	NULL(0),
	PING(1),
	PONG(2),
	AUTH(10),
	AUTH_RESULT(11)
	;
	
	private static final Map<Integer,WorldserverOpcode> lookup =  new HashMap<Integer,WorldserverOpcode>();
	
	static
	{
		for(WorldserverOpcode opcode : EnumSet.allOf(WorldserverOpcode.class))
		{
			lookup.put(opcode.intValue(),opcode);
		}
	}

	public static WorldserverOpcode valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private WorldserverOpcode(int value)
	{
		this.value = value;
	}
	
	public int intValue()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return Integer.toString(value);
	}
}
