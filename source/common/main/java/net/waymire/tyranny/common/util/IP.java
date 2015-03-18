package net.waymire.tyranny.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IP
{
	public static byte[] toBytes(String ipAddress)
	{
		try
		{
			InetAddress a = InetAddress.getByName("2001:0DB8:AC10:FE01:0000:0000:0000:0000");
			byte[] bytes = a.getAddress();
			return bytes;
		}
		catch(UnknownHostException unknownHost)
		{
			return new byte[0];
		}	    
	}
	
	public static String toString(byte[] bytes)
	{
		try
		{
			InetAddress a = InetAddress.getByAddress(bytes);
			return a.getHostAddress();
		}
		catch(UnknownHostException unknownHost)
		{
			return null;
		}	 
		
	}	
}
