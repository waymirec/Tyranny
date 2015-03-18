package net.waymire.tyranny.common.util;

import java.util.Random;

public class MathUtil {
	
	public static int random(int min,int max)
	{
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		return rand.nextInt(max - min + 1) + min;
	}
}
