package net.waymire.tyranny.common.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceTranslator 
{
	private final ResourceBundle bundle;
	private final MessageFormat formatter = new MessageFormat("");


	public ResourceTranslator(String baseName)
	{
		this.bundle = ResourceBundle.getBundle(baseName);
	}
	
	public ResourceTranslator(String baseName, Locale locale)
	{
		this.bundle = ResourceBundle.getBundle(baseName, locale);
	}
	
	public String translate(String key, Object...args)
	{
		formatter.applyPattern(bundle.getString(key));
		return formatter.format(args);
	}
}
