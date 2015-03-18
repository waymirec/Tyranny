package net.waymire.tyranny.common;

import java.net.InetAddress;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class ClientSystemInformation {
	private InetAddress ip;
	private String country;
	private String operatingSystem;
	private String operatingSystemVersion;
	private String platform;
	private String dataModel;
	private String javaVersion;
	private String vmVendor;
	private String vmVersion;
	private String owner;
	private String language;
	private String endian;
	
	public void setIp(InetAddress ip) 
	{
		this.ip = ip;
	}
	
	public InetAddress getIp() 
	{
		return ip;
	}
	
	public void setCountry(String country) 
	{
		this.country = country;
	}
	
	public String getCountry() 
	{	
		return country;
	}
	
	public void setOperatingSystem(String os) 
	{
		this.operatingSystem = os;
	}
	
	public String getOperatingSystem() 
	{
		return operatingSystem;
	}

	public void setOperatingSystemVersion(String version) 
	{
		this.operatingSystemVersion = version;
	}
	
	public String getOperatingSystemVersion() 
	{
		return operatingSystemVersion;
	}

	public void setPlatform(String platform) 
	{
		this.platform = platform;
	}
	
	public String getPlatform() 
	{
		return platform;
	}

	public void setDataModel(String dataModel)
	{
		this.dataModel = dataModel;
	}
	
	public String getDataModel()
	{
		return dataModel;
	}
	
	public void setJavaVersion(String javaVersion) 
	{
		this.javaVersion = javaVersion;
	}
	
	public String getJavaVersion() 
	{
		return javaVersion;
	}
	
	public void setVirtualMachineVendor(String vendor)
	{
		this.vmVendor = vendor;
	}
	
	public String getVirtualMachineVendor()
	{
		return vmVendor;
	}
	
	public void setVirtualMachineVersion(String version)
	{
		this.vmVersion = version;
	}
	
	public String getVirtualMachineVersion()
	{
		return vmVersion;
	}
	
	public void setOwner(String owner) 
	{
		this.owner = owner;
	}
	
	public String getOwner() 
	{
		return owner;
	}
	
	public void setLanguage(String language) 
	{
		this.language = language;
	}
	
	public String getLanguage() 
	{
		return language;
	}
	
	public void setEndian(String endian) 
	{
		this.endian = endian;
	}
	
	public String getEndian() 
	{
		return endian;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}