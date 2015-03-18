package net.waymire.tyranny.common;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class ClientGameInformation {
	private String game;
	private int majorVersion;
	private int minorVersion;
	private int maintVersion;
	private int build;
	private String username;
	
	public void setGame(String game)
	{
		this.game = game;
	}
	
	public String getGame() {
		return game;
	}
	
	public void setMajorVersion(int majorVersion)
	{
		this.majorVersion = majorVersion;
	}
	
	public int getMajorVersion()
	{
		return majorVersion;
	}
	
	public void setMinorVersion(int minorVersion)
	{
		this.minorVersion = minorVersion;
	}
	
	public int getMinorVersion()
	{
		return minorVersion;
	}
	
	public void setMaintenanceVersion(int maintVersion)
	{
		this.maintVersion = maintVersion;
	}
	
	public int getMaintenanceVersion()
	{
		return maintVersion;
	}
	
	public String getVersion() {
		return new StringBuilder().
			append(majorVersion).
			append(".").
			append(minorVersion).
			append(".").
			append(maintVersion).
			toString();
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setBuild(int build)
	{
		this.build = build;
	}
	
	public int getBuild()
	{
		return build;
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}