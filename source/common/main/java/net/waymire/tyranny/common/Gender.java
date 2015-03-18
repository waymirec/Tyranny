package net.waymire.tyranny.common;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Gender {
	MALE('M'),
	FEMALE('F'),
	;
	
	private static final Map<Character,Gender> lookup =  new HashMap<Character,Gender>();
	
	static
	{
		for(Gender opcode : EnumSet.allOf(Gender.class))
		{
			lookup.put(opcode.value(),opcode);
		}
	}

	public static Gender valueOf(Character value)
	{
		return lookup.get(value);
	}
	
	private Character value;

	private Gender(Character value)
	{
		this.value = value;
	}
	
	public Character value()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return Character.toString(value);
	}
}
