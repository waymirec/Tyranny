package net.waymire.tyranny.common.protocol;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AuthControlAuthOpcode implements Opcode
{
	IDENT(1),
	IDENT_RESP(2),
	
	CHAP_CHALLENGE(10),
	CHAP_PROOF(11),
	CHAP_RESPONSE(12),
	
	READY(20),
;
	
	private static final Map<Integer,AuthControlAuthOpcode> lookup =  new HashMap<Integer,AuthControlAuthOpcode>();
	
	static
	{
		for(AuthControlAuthOpcode opcode : EnumSet.allOf(AuthControlAuthOpcode.class))
		{
			lookup.put(opcode.intValue(),opcode);
		}
	}

	public static AuthControlAuthOpcode valueOf(int value)
	{
		return lookup.get(value);
	}
	
	private int value;

	private AuthControlAuthOpcode(int value)
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
