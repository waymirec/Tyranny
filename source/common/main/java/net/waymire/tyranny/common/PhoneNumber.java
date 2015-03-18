package net.waymire.tyranny.common;

import java.io.Serializable;

public class PhoneNumber implements Serializable
{
	static final long serialVersionUID = -1L;
	
	private Short areaCode = null;
	private Short prefix = null;
	private Short line = null;
	private Integer flags = 0;
	
	public PhoneNumber()
	{
		
	}
	
	public PhoneNumber(Short areaCode, Short prefix, Short line)
	{
		this.areaCode = areaCode;
		this.prefix = prefix;
		this.line = line;
	}
	
	public Short getAreaCode()
	{
		return areaCode;
	}
	
	public void setAreaCode(Short areaCode)
	{
		this.areaCode = areaCode;
	}
	
	public Short getPrefix()
	{
		return prefix;
	}
	
	public void setPrefix(Short prefix)
	{
		this.prefix = prefix;
	}
	
	public Short getLine()
	{
		return line;
	}
	
	public void setLine(Short line)
	{
		this.line = line;
	}

	public Integer getFlags()
	{
		return flags;
	}

	public void setFlags(Integer flags)
	{
		this.flags = flags;
	}
	
	@Override
	public String toString()
	{
		return String.format("%03d-%03d-%04d",areaCode,prefix,line);
	}
}
