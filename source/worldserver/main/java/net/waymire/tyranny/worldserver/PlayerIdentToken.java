package net.waymire.tyranny.worldserver;

import java.net.InetAddress;

import net.waymire.tyranny.common.GUID;

public class PlayerIdentToken 
{
	private final long timestamp;

	private GUID id = GUID.generate();
	private GUID accountId;
	private InetAddress inetAddress;
	
	public PlayerIdentToken()
	{
		this.timestamp = System.currentTimeMillis();
	}
	
	public long getTimestamp()
	{
		return timestamp;
	}

	public GUID getId() 
	{
		return id;
	}

	public void setId(GUID id) 
	{
		this.id = id;
	}

	public GUID getAccountId() 
	{
		return accountId;
	}

	public void setAccountId(GUID accountId) 
	{
		this.accountId = accountId;
	}

	public InetAddress getInetAddress() 
	{
		return inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) 
	{
		this.inetAddress = inetAddress;
	}
}
