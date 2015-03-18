package net.waymire.tyranny.common.util;

import java.nio.ByteOrder;

final public class PlatformUtil {
	public static final int LITTLE_ENDIAN = 1;
	public static final int BIG_ENDIAN = 2;

	private PlatformUtil() { }
	
	public static int getSystemEndian()
	{
		ByteOrder b = ByteOrder.nativeOrder();
		return b.equals(ByteOrder.LITTLE_ENDIAN) ? 1 : 2;
	}
}
