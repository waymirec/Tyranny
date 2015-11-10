package net.waymire.tyranny.common.protocol;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum LoginserverOpcode implements Opcode {
	NULL(0),
	PING(1),
	PONG(2),
	AUTH(10),
	AUTH_RESULT(11),
	
	CLOCK_SYNC_REQ(20),
	CLOCK_SYNC_RES(21),
	
	WORLD_INFO(30),
	
	GO_IDENT(40),
	GO_TRANSFORM(41),
	GO_MOVE(42),
	;
	
	private static final Map<Integer,LoginserverOpcode> lookup =  new HashMap<Integer,LoginserverOpcode>();
	
	static
	{
		for(LoginserverOpcode opcode : EnumSet.allOf(LoginserverOpcode.class))
		{
			lookup.put(opcode.intValue(),opcode);
		}
	}

	public static LoginserverOpcode valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private LoginserverOpcode(int value)
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
