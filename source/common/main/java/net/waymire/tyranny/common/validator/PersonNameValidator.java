package net.waymire.tyranny.common.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import net.waymire.tyranny.common.PersonName;

public class PersonNameValidator
{
	private final Pattern pattern;
	private Matcher matcher;
	
	private static final String FULLNAME_PATTERN = "^(?:[A-Za-z]{3,9}\\s)(?:[A-Z]\\.\\s){0,1}[A-Za-z]{3,9}$";
	 
	public PersonNameValidator() 
	{
		pattern = Pattern.compile(FULLNAME_PATTERN);
	}
 
	/**
	 * Validate person name string
	 * 
	 * @param fullname name for validation
	 * @return true valid name, false invalid name
	 */
	public boolean validate(final String name) 
	{
 
		matcher = pattern.matcher(name);
		return matcher.matches();
	}
	
	/**
	 * Validate person name object
	 * 
	 * @param fullname name for validation
	 * @return true valid name, false invalid name
	 */
	public boolean validate(final PersonName name)
	{
		if(name == null)
		{
			return false;
		}
		
		// Last name is required
		if(StringUtils.isEmpty(name.getLastname()))
		{
			return false;
		}
				
		// First name is not required, but a middle name with no first name is not allowed
		if(!StringUtils.isEmpty(name.getMiddlename()) && StringUtils.isEmpty(name.getFirstname()))
		{
			return false;
		}
		return true;
	}
}
