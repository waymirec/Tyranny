package net.waymire.tyranny.client.message;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MovementAction {
	FORWARD((short)1),
	BACKWARD((short)2),
	LEFT((short)3),
	RIGHT((short)4),
	JUMP((short)5),
	ROTATE_LEFT((short)6),
	ROTATE_RIGHT((short)7),
	STRAFE((short)8),
	;
	
	private static final Map<Short,MovementAction> lookup =  new HashMap<Short,MovementAction>();
	
	static
	{
		for(MovementAction opcode : EnumSet.allOf(MovementAction.class))
		{
			lookup.put(opcode.value(),opcode);
		}
	}

	public static MovementAction valueOf(Short value)
	{
		return lookup.get(value);
	}
	
	private Short value;

	private MovementAction(Short value)
	{
		this.value = value;
	}
	
	public Short value()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return Short.toString(value);
	}
}
