package net.waymire.tyranny.common;

import java.net.InetAddress;


public class World {

	private GUID guid = null;
	private String hostname = null;
	private String worldname = null;
	private InetAddress inetAddress = null;
	private Integer port = null;
	
	public World()
	{

	}
	
	public GUID getGUID()
	{
		return guid;
	}
	
	public void setGUID(GUID guid)
	{
		this.guid = guid;
	}
	
	public String getHostname()
	{
		return hostname;
	}
	
	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}
	
	public String getWorldname()
	{
		return worldname;
	}
	
	public void setWorldname(String worldname)
	{
		this.worldname = worldname;
	}
	
	public InetAddress getInetAddress()
	{
		return inetAddress;
	}
	
	public void setInetAddress(InetAddress inetAddress)
	{
		this.inetAddress = inetAddress;
	}
	
	public Integer getPort()
	{
		return port;
	}
	
	public void setPort(Integer port)
	{
		this.port = port;
	}
	
	public String toString()
	{
		return hostname;
	}
}
