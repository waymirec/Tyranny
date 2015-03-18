package net.waymire.tyranny.worldserver.net;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum WorldserverSessionState 
{
	NULL(0),
	ERR(1),
	PENDING_IDENT(2),
	IDENT_RCVD(3),
	;
	
	private static final Map<Integer,WorldserverSessionState> lookup =  new HashMap<Integer,WorldserverSessionState>();
	
	static
	{
		for(WorldserverSessionState state : EnumSet.allOf(WorldserverSessionState.class))
		{
			lookup.put(state.intValue(),state);
		}
	}

	public static WorldserverSessionState valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private WorldserverSessionState(int value)
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
