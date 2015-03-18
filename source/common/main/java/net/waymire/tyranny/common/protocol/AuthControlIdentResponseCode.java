package net.waymire.tyranny.common.protocol;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AuthControlIdentResponseCode 
{
	SUCCESS((byte)0),
	VERSION_MISMATCH((byte)1),
	INVALID_KEY((byte)2),
	;
	
	private static final Map<Byte,AuthControlIdentResponseCode> lookup =  new HashMap<>();
	
	static
	{
		for(AuthControlIdentResponseCode code : EnumSet.allOf(AuthControlIdentResponseCode.class))
		{
			lookup.put(code.byteValue(),code);
		}
	}

	public static AuthControlIdentResponseCode valueOf(byte value)
	{
		return lookup.get(value);
	}
	
	private byte value;

	private AuthControlIdentResponseCode(byte value)
	{
		this.value = value;
	}
	
	public int intValue()
	{
		return (int)value;
	}
	
	public byte byteValue()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return Integer.toString(value);
	}
}
