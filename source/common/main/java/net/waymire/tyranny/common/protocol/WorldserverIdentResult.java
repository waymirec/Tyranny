package net.waymire.tyranny.common.protocol;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum WorldserverIdentResult 
{
	VALID_TOKEN((short)1),
	INVALID_TOKEN((short)2),
	UNEXPECTED_ERROR((short)9999)
	;
	
	private static final Map<Short,WorldserverIdentResult> lookup =  new HashMap<Short,WorldserverIdentResult>();
	
	static
	{
		for(WorldserverIdentResult IdentResult : EnumSet.allOf(WorldserverIdentResult.class))
		{
			lookup.put(IdentResult.shortValue(),IdentResult);
		}
	}

	public static WorldserverIdentResult valueOf(short value)
	{
		return lookup.get(value);
	}
	
	private short value;

	private WorldserverIdentResult(short value)
	{
		this.value = value;
	}
	
	public int intValue()
	{
		return (int)value;
	}
	
	public short shortValue()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return Integer.toString(value);
	}
}
