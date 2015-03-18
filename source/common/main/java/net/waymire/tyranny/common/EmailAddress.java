package net.waymire.tyranny.common;

import java.io.Serializable;

/**
 * Complex object representing an email address.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public class EmailAddress implements Serializable
{
	static final long serialVersionUID = -1L;
	
	private String value;
	
	public EmailAddress()
	{
		
	}
	
	public EmailAddress(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
}
