package net.waymire.tyranny.common.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

final public class URLFactory {
	private static final Logger LOGGER = Logger.getLogger(URLFactory.class.getName());
	
	private URLFactory() { }
	
	public static URL createURL(String urlstring)
	{
		URL url = null;
		try
		{
			url = new URL(urlstring);
		}
		catch(MalformedURLException mue)
		{
			LOGGER.log(Level.SEVERE,ExceptionUtil.getReason(mue));
			LOGGER.log(Level.SEVERE,ExceptionUtil.getStackTrace(mue));
			// do nothing. a null return value is the indicator of a problem.
		}
		return url;
	}
}
