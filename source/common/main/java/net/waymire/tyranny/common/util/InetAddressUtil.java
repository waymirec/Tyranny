package net.waymire.tyranny.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class InetAddressUtil {
	
	private InetAddressUtil() { }
	
	public static InetAddress long2Inet(long ip)
	{
		byte[] bytes = new byte[4];
		bytes[0] = (byte)((ip >>> 24) & 0xFF);
		bytes[1] = (byte)((ip >>> 16) & 0xFF);
		bytes[2] = (byte)((ip >>> 8) & 0xFF);
		bytes[3] = (byte)(ip & 0xFF);
		
		try
		{
			return InetAddress.getByAddress(bytes);
		}
		catch(UnknownHostException uhe)
		{
			return null;
		}
	}
	
	public static Long inet2Long(InetAddress ip)
	{
		byte[] bytes = ip.getAddress();
		return (long)((bytes[3]) | (bytes[2] << 8) | (bytes[1] << 16) | (bytes[0] << 24));
	}

	public static Long hostname2Long(String hostname)
	{
		InetAddress inetAddr = getByName(hostname);
		if(inetAddr != null)
		{
			return inet2Long(inetAddr);
		}
		return null;
	}
	
	public static String long2Hostname(long ip)
	{
		InetAddress inetAddr = long2Inet(ip);
		if(inetAddr != null)
		{
			return inetAddr.getHostAddress();
		}
		return null;
	}
	
	public static InetAddress subnet(InetAddress ip, int mask)	
	{		
		long subnet = InetAddressUtil.inet2Long(ip);
		long base = 0xFFFFFFFFL;
		
		long hosts = base >> mask;
		long network = (base ^ hosts) & subnet;
		
		return InetAddressUtil.long2Inet(network);
	}
	
	public static InetAddress broadcast(InetAddress ip, int mask)
	{
		long subnet = InetAddressUtil.inet2Long(ip);
		long base = 0xFFFFFFFFL;
		
		long hosts = base >> mask;
		long bcast = hosts | subnet;

		return InetAddressUtil.long2Inet(bcast);
	}
	
	/*
	public static boolean InRange(String ip, String subnet, int mask) throws Exception
	{
		byte[] bytes = IPAddr.IP2Bytes(ip);
		byte[] network = IPAddr.IP2Bytes(subnet);
		
		int fullMask = -1 << (32 - mask);
		byte netmask[] = new byte[4];		
		netmask[0] = (byte) ((fullMask & 0xFF000000) >>> 24);
        netmask[1] = (byte) ((fullMask & 0x00FF0000) >>> 16);
        netmask[2] = (byte) ((fullMask & 0x0000FF00) >>> 8);
        netmask[3] = (byte) (fullMask & 0x000000FF);
        
        for (int i=0; i<4; i++)
        {
        	if ((bytes[i] & netmask[i]) != (network[i] & netmask[i]))
        	{
        		return false;
        	}        
        }
        
        return true;
	}	
	*/
	
	public static InetAddress getLocalhost()
	{
		try
		{
			return InetAddress.getLocalHost();
		}
		catch(UnknownHostException uhe)
		{
			return null;
		}
	}
	
	public static InetAddress getByName(String hostname)
	{
		try
		{
			return InetAddress.getByName(hostname);
		}
		catch(UnknownHostException uhe)
		{
			return null;
		}
	}
}
