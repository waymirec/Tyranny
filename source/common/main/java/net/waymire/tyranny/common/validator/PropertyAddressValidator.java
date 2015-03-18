package net.waymire.tyranny.common.validator;

import org.apache.commons.lang.StringUtils;

import net.waymire.tyranny.common.PropertyAddress;

public class PropertyAddressValidator
{

	public boolean validate(final PropertyAddress address)
	{
		if(address == null)
		{
			return false;
		}
		
		if(StringUtils.isEmpty(address.getAddress1()))
		{
			return false;
		}
		
		if(StringUtils.isEmpty(address.getCity()))
		{
			return false;
		}
		
		if(StringUtils.isEmpty(address.getState()))
		{
			return false;
		}
		
		return true;
	}
}
