package net.waymire.tyranny.common.net;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TcpSessionState {
	CONNECTED(1),
	READY(2),
	IDLE(3),
	ERR(4),
	DISCONNECTED(5),
	;
	
	private static final Map<Integer,TcpSessionState> lookup =  new HashMap<Integer,TcpSessionState>();
	
	static
	{
		for(TcpSessionState state : EnumSet.allOf(TcpSessionState.class))
		{
			lookup.put(state.intValue(),state);
		}
	}

	public static TcpSessionState valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private TcpSessionState(int value)
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
