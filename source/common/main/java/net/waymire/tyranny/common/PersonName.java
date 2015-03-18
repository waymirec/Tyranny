package net.waymire.tyranny.common;

import java.io.Serializable;

public class PersonName implements Serializable
{
	static final long serialVersionUID = -1L;
	
	private String firstname = null;
	private String middlename = null;
	private String lastname = null;
	
	public PersonName()
	{
		
	}
	
	public PersonName(String firstname, String middlename, String lastname)
	{
		this.firstname = firstname;
		this.middlename = middlename;
		this.lastname = lastname;
	}
	
	public PersonName(String firstname, String lastname)
	{
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	public String getFirstname()
	{
		return firstname;
	}
	
	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}
	
	public String getMiddlename()
	{
		return middlename;
	}
	
	public void setMiddlename(String middlename)
	{
		this.middlename = middlename;
	}
	
	public String getLastname()
	{
		return lastname;
	}
	
	public void setLastname(String lastname)
	{
		this.lastname = lastname;
	}
}
