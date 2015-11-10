package net.waymire.tyranny.client.net;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum LoginserverClientSessionState 
{
	NULL(0),
	ERR(1),
	CONNECTED(2),
	PENDING_AUTH(3),
	AUTHENTICATED(4),
	AUTH_FAILED(6)
	;
	
	private static final Map<Integer,LoginserverClientSessionState> lookup =  new HashMap<>();
	
	static
	{
		for(LoginserverClientSessionState state : EnumSet.allOf(LoginserverClientSessionState.class))
		{
			lookup.put(state.intValue(),state);
		}
	}

	public static LoginserverClientSessionState valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private LoginserverClientSessionState(int value)
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
