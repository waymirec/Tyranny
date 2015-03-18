package net.waymire.tyranny.common.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.waymire.tyranny.common.PhoneNumber;

public class PhoneNumberValidator
{
	private final Pattern pattern;
	private Matcher matcher;
 
	//private static final String PHONE_PATTERN = "^([\\+(]?(\\d){2,}[)]?[- \\.]?(\\d){2,}[- \\.]?(\\d){2,}[- \\.]?(\\d){2,}[- \\.]?(\\d){2,})|([\\+(]?(\\d){2,}[)]?[- \\.]?(\\d){2,}[- \\.]?(\\d){2,}[- \\.]?(\\d){2,})|([\\+(]?(\\d){2,}[)]?[- \\.]?(\\d){2,}[- \\.]?(\\d){2,})$";
	private static final String PHONE_PATTERN = "^\\d{7}|\\d{10}$";
	
	public PhoneNumberValidator() 
	{
		pattern = Pattern.compile(PHONE_PATTERN);
	}
 
	/**
	 * Validate phone number string
	 * 
	 * @param phone phone number for validation
	 * @return true valid phone number, false invalid phone number
	 */
	public boolean validate(final String phone) 
	{
		String stripped = phone.replaceAll("[^0-9]", "");
		matcher = pattern.matcher(stripped);
		return matcher.matches();
	}
	
	/**
	 * Validate phone number object
	 * 
	 * @param phone phone number for validation
	 * @return true valid phone number, false invalid phone number
	 */
	public boolean validate(PhoneNumber phone)
	{
		if(phone == null)
		{
			return false;
		}
		
		if(phone.getAreaCode() == null || phone.getAreaCode().toString().length() != 3)
		{
			return false;
		}
		
		if(phone.getPrefix() == null || phone.getPrefix().toString().length() != 3)
		{
			return false;
		}
		
		if(phone.getLine() == null || phone.getLine().toString().length() != 4)
		{
			return false;
		}
		
		return true;
	}
}
