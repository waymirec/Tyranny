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
	
	CHAR_LIST_REQ(50),
	CHAR_LIST(51),
	CHAR_CREATE_REQ(52),
	CHAR_CREATED(53),
	CHAR_CREATE_FAILED(54),
	CHAR_DELETE_REQ(55),
	CHAR_DELETED(56),
	CHAR_DELETE_FAILED(57),

	ENTER_WORLD(70),
	
	GO_IDENT(80),
	GO_TRANSFORM(81),
	GO_MOVE(82),
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
