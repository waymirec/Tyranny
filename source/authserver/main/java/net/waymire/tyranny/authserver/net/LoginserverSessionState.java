package net.waymire.tyranny.authserver.net;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum LoginserverSessionState 
{
	NULL(0),
	ERR(1),
	PENDING_IDENT(2),
	IDENT_RCVD(3),
	AUTH_PROOF_RCVD(4),
	AUTHENTICATED(5),
	AUTH_FAILED(6),
	;
	
	private static final Map<Integer,LoginserverSessionState> lookup =  new HashMap<Integer,LoginserverSessionState>();
	
	static
	{
		for(LoginserverSessionState state : EnumSet.allOf(LoginserverSessionState.class))
		{
			lookup.put(state.intValue(),state);
		}
	}

	public static LoginserverSessionState valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private LoginserverSessionState(int value)
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
