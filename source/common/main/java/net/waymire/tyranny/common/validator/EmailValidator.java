package net.waymire.tyranny.common.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import net.waymire.tyranny.common.EmailAddress;

public class EmailValidator
{
	private final Pattern pattern;
	private Matcher matcher;
 
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
 
	public EmailValidator() 
	{
		pattern = Pattern.compile(EMAIL_PATTERN);
	}
 
	/**
	 * Validate email string
	 * 
	 * @param email email for validation
	 * @return true valid email, false invalid email
	 */
	public boolean validate(final String email) 
	{
 
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	/**
	 * Validate email object
	 * 
	 * @param email email for validation
	 * @return true valid email, false invalid email
	 */
	public boolean validate(final EmailAddress email)
	{
		if(email == null)
		{
			return false;
		}
		
		if(StringUtils.isEmpty(email.getValue()))
		{
			return false;
		}
		
		if(!validate(email.getValue()))
		{
			return false;
		}
		
		return true;
	}
}
