package net.waymire.tyranny.common;

import java.io.Serializable;

public class PropertyAddress implements Serializable
{
	static final long serialVersionUID = -1L;
	
	private String address1;
	private String address2;
	private String city;
	private String state;
	private Integer zip;
	private String country;
	
	public String getAddress1()
	{
		return address1;
	}
	
	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}
	
	public String getAddress2()
	{
		return address2;
	}
	
	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}
	
	public String getCity()
	{
		return city;
	}
	
	public void setCity(String city)
	{
		this.city = city;
	}
	
	public String getState()
	{
		return state;
	}
	
	public void setState(String state)
	{
		this.state = state;
	}
	
	public Integer getZip()
	{
		return zip;
	}
	
	public void setZip(Integer zip)
	{
		this.zip = zip;
	}
	
	public String getCountry()
	{
		return country;
	}
	
	public void setCountry(String country)
	{
		this.country = country;
	}
}