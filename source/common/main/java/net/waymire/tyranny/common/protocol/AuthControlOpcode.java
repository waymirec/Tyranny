package net.waymire.tyranny.common.protocol;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AuthControlOpcode implements Opcode {
	NULL(0),
	PING(1),
	PONG(2),
	AUTH(10),
	AUTH_RESULT(11),
	
	CLOCK_SYNC_REQ(20),
	CLOCK_SYNC_RES(21),
	
	PLAYER_IDENT_TOKEN_REQ(30),
	PLAYER_IDENT_TOKEN_RSP(31),
	;
	
	private static final Map<Integer,AuthControlOpcode> lookup =  new HashMap<Integer,AuthControlOpcode>();
	
	static
	{
		for(AuthControlOpcode opcode : EnumSet.allOf(AuthControlOpcode.class))
		{
			lookup.put(opcode.intValue(),opcode);
		}
	}

	public static AuthControlOpcode valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private AuthControlOpcode(int value)
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