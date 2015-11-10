package net.waymire.tyranny.common.protocol;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum WorldserverOpcode implements Opcode {
	NULL(0),
	PING(1),
	PONG(2),
	IDENT(10),
	IDENT_RESULT(11),
	
	CHAR_LIST_REQ(20),
	CHAR_LIST(21),
	CHAR_ADD(22),
	CHAR_ADD_ACK(23),
	CHAR_ADD_NACK(24),
	CHAR_DEL(25),
	CHAR_DEL_ACK(26),
	CHAR_DEL_NACK(27),

	ENTER_WORLD_REQ(30),
	ENTER_WORLD(31),
	
	PLAYER_MOVE_STRAFE(100),
	PLAYER_MOVE_FORWARD(101),
	PLAYER_MOVE_BACKWARD(102),
	PLAYER_MOVE_LEFT(103),
	PLAYER_MOVE_RIGHT(104),
	PLAYER_MOVE_ROTATE_LEFT(105),
	PLAYER_MOVE_ROTATE_RIGHT(106),
	PLAYER_MOVE_JUMP(107),
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
