package net.waymire.tyranny.worldserver.net;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AuthControlClientSessionState 
{
	NULL(0),
	ERR(1),
	CONNECTED(2),
	PENDING_AUTH(3),
	AUTHENTICATED(4),
	AUTH_FAILED(6)
	;
	
	private static final Map<Integer,AuthControlClientSessionState> lookup =  new HashMap<>();
	
	static
	{
		for(AuthControlClientSessionState state : EnumSet.allOf(AuthControlClientSessionState.class))
		{
			lookup.put(state.intValue(),state);
		}
	}

	public static AuthControlClientSessionState valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private AuthControlClientSessionState(int value)
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
