package net.waymire.tyranny.common;

import java.io.Serializable;
import java.util.UUID;

/**
 * A Globally Unique Identifier is a unique reference number stored as a 128-bit value
 * and displayed as 32 hexadecimal digits with groups separated by hyphens, such as
 * {21EC2020-3AEA-4069-A2DD-08002B30309D}. 
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public final class GUID implements Serializable 
{
	public static final GUID EMPTY = GUID.generate("00000000-0000-0000-0000-000000000000");
	
	static final long serialVersionUID = -1L;

	@EqualsContributor
	private UUID uuid;

	public GUID()
	{
		long now = System.currentTimeMillis();
		this.uuid = new UUID(now, UUID.randomUUID().getLeastSignificantBits());
	}
	
	public GUID(String value)
	{
		this.uuid = UUID.fromString(value);
	}
	
	public static boolean equals(GUID first, GUID second)
	{
		if(first != null && second != null)
		{
			return first.equals(second);
		}
		return false;
	}
	
	public static final GUID generate()
	{
		UUID uuid = UUID.randomUUID();
		long now = System.currentTimeMillis();
		return GUID.generate(now,uuid.getLeastSignificantBits());
	}

	public static final GUID generate(String input)
	{
		UUID uuid = UUID.fromString(input);
		return new GUID(uuid);
	}
	
	public static final GUID generate(byte[] bytes)
	{
		UUID uuid = UUID.nameUUIDFromBytes(bytes);
		return new GUID(uuid);
	}
	
	public static final GUID generate(long mostSigBits,long leastSigBits)
	{
		UUID uuid = new UUID(mostSigBits,leastSigBits);
		return new GUID(uuid);
	}

	private GUID(UUID uuid)
	{
		this.uuid = uuid;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	public long getMostSignificantBits()
	{
		return uuid.getMostSignificantBits();
	}
	
	public long getLeastSignificantBits()
	{
		return uuid.getLeastSignificantBits();
	}
	
	@Override
	public String toString()
	{
		return uuid.toString();
	}
	
	@Override
	public int hashCode()
	{
		return uuid.hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other != null)
		{
			if(other instanceof GUID)
			{
				return uuid.toString().equals(other.toString());
			}
		}
		return false;
	}
	
	public int compareTo(GUID other)
	{
		return this.uuid.compareTo(other.getUUID());
	}
	
	@SuppressWarnings("unused")
	private void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}
}
