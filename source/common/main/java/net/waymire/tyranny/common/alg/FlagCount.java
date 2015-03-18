package net.waymire.tyranny.common.alg;

public class FlagCount {

	public static void main(String[] args)
	{
		int c1 = FlagCount.count(10);
		int c2 = FlagCount.count(13);
		int c3 = FlagCount.count(15);
		int c4 = FlagCount.count(21845);
		
		System.out.printf("10 == %d\n", c1);
		System.out.printf("13 == %d\n", c2);
		System.out.printf("15 == %d\n", c3);
		System.out.printf("21845 == %d\n", c4);
	}
	
	public static int count(int x)
	{
		if(x > 0)
		{
			int count = 1;
			while((x = (x & (x-1))) > 0)
			{
				count++;
			}
			return count;
		}
		return 0;
	}
}
