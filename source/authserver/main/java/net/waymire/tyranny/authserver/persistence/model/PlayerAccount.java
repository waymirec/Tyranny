package net.waymire.tyranny.authserver.persistence.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import net.waymire.tyranny.common.GUID;

@Entity("player_account")
public class PlayerAccount 
{
	@Id
	private ObjectId id;
	private GUID accountId;
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private String middleInitial;
	
	public GUID getAccountId() 
	{
		return accountId;
	}
	
	public void setAccountId(GUID accountId) 
	{
		this.accountId = accountId;
	}
	
	public String getUsername() 
	{
		return username;
	}
	
	public void setUsername(String username) 
	{
		this.username = username;
	}
	
	public String getPassword() 
	{
		return password;
	}
	
	public void setPassword(String password) 
	{
		this.password = password;
	}
	
	public String getFirstname() 
	{
		return firstname;
	}
	
	public void setFirstname(String firstname) 
	{
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	
	public void setLastname(String lastname) 
	{
		this.lastname = lastname;
	}
	
	public String getMiddleInitial() 
	{
		return middleInitial;
	}
	
	public void setMiddleInitial(String middleInitial) 
	{
		this.middleInitial = middleInitial;
	}
}