package net.waymire.tyranny.common.message;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MessagePriority
{
	LOW(-1),
	NORMAL(0),
	MEDIUM(1),
	HIGH(2),
	URGENT(3)
	;
	
	private static final Map<Integer,MessagePriority> lookup =  new HashMap<Integer,MessagePriority>();
	
	static
	{
		for(MessagePriority opcode : EnumSet.allOf(MessagePriority.class))
		{
			lookup.put(opcode.intValue(),opcode);
		}
	}

	public static MessagePriority valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private MessagePriority(int value)
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
