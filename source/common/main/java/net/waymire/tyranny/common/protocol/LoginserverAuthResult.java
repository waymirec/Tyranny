package net.waymire.tyranny.common.protocol;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the result of a game client attempting to authenticate to the 
 * Auth Server.
 *
 */
public enum LoginserverAuthResult {
	/* Account is authenticated, active and ready for play */
	READY((short)1),
	/* Invalid credentials were supplied. Authentication failed.*/
	INVALID_CREDENTIALS((short)2),
	/* Valid credentials were provided but the account does not have an active subscsription */
	BILLING_EXPIRED((short)3),
	/* Valid credentials were provided but the account has been frozen. Contact customer support for details */
	FROZEN((short)4),
	/* Unknown and unexpected error */
	UNEXPECTED_ERROR((short)9999)
	;
	
	private static final Map<Short,LoginserverAuthResult> lookup =  new HashMap<Short,LoginserverAuthResult>();
	
	static
	{
		for(LoginserverAuthResult authResult : EnumSet.allOf(LoginserverAuthResult.class))
		{
			lookup.put(authResult.shortValue(),authResult);
		}
	}

	public static LoginserverAuthResult valueOf(short value)
	{
		return lookup.get(value);
	}
	
	private short value;

	private LoginserverAuthResult(short value)
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