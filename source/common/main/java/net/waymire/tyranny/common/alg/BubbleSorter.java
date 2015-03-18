package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Order of growth is big theta of n-squared
 * Comparisons = (n-1) + (n-2) + ... + 1
 * 			   = n(n-1)/2
 * 			   = n(sq) - n/2
 * 			   = O(n(sq))
 * 
 * 
 * @param <T>
 */
public class BubbleSorter<T extends Comparable<T>> implements Sorter<T>
{

	public static void main(String[] args)
	{
		
		List<Integer> list = new ArrayList<Integer>();
		Random generator = new Random(System.currentTimeMillis());
		System.out.println("Generating random numbers...");
		long start = System.currentTimeMillis();
		for(int i=0; i<10000; i++)
		{
			list.add(generator.nextInt());
		}
		long stop = System.currentTimeMillis();
		System.out.println("Completed in " + (stop - start) + "ms" + "\n");
		
		BubbleSorter<Integer> bs = new BubbleSorter<Integer>();
		System.out.printf("Sorting [%d] elements...\n",list.size());
		start = System.currentTimeMillis();
		bs.sort(list);
		stop = System.currentTimeMillis();
		
		System.out.println("Completed in " + (stop - start) + "ms" + "\n");
		for(int i=0; i<list.size()-1; i++)
		{
			if(list.get(i).compareTo(list.get(i+1)) > 0)
			{
				System.out.println("Array is not propertly sorted.");
				for(Integer j : list) { System.out.print(j + ","); }
				return;
			}
		}
		System.out.println("Array is properly sorted.");		
	}
	
	public List<T> sort(List<T> input)
	{
		if(input.size() <= 1)
			return input;
		
		for(int i=(input.size()-1); i>=1; i--)
		{
			for(int j=0; j<=i-1; j++)
			{
				if(input.get(j).compareTo(input.get(j+1)) >= 0)
				{
					T tmp = input.get(j+1);
					input.set(j+1, input.get(j));
					input.set(j, tmp);
				}
			}
		}
		return input;
	}	
}
