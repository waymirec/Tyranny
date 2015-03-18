package net.waymire.tyranny.common;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class ClientInformation {
	private final ClientSystemInformation systemInformation;
	private final ClientGameInformation gameInformation;	
	
	public ClientInformation(ClientSystemInformation systemInformation,ClientGameInformation gameInformation)
	{
		this.systemInformation = systemInformation;
		this.gameInformation = gameInformation;
	}
	
	public ClientSystemInformation getSystemInformation()
	{
		return systemInformation;
	}
	
	public ClientGameInformation getGameInformation()
	{
		return gameInformation;
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
