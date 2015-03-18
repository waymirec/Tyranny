package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SelectionSorter<T extends Comparable<T>> implements Sorter<T> {

	public static void main(String[] args)
	{
		
		List<Integer> list = new ArrayList<Integer>();
		Random generator = new Random(System.currentTimeMillis());
		System.out.println("Generating random numbers...");
		long start = System.currentTimeMillis();
		for(int i=0; i<100000; i++)
		{
			list.add(generator.nextInt());
		}
		long stop = System.currentTimeMillis();
		System.out.println("Completed in " + (stop - start) + "ms" + "\n");
		
		Sorter<Integer> bs = new SelectionSorter<Integer>();
		System.out.printf("Sorting [%d] elements...\n",list.size());
		start = System.currentTimeMillis();
		bs.sort(list);
		stop = System.currentTimeMillis();
		
		System.out.println("Completed in " + (stop - start) + "ms" + "\n");
		for(int i=0; i<list.size()-1; i++)
		{
			if(list.get(i).compareTo(list.get(i+1)) > 0)
			{
				System.out.println("Array is not property sorted.");
				for(Integer j : list) { System.out.print(j + ","); }
				return;
			}
		}
		System.out.println("Array is properly sorted.");		
	}
	
	public List<T> sort(List<T> input)
	{
		for(int i=0; i < (input.size()-1); i++)
		{
			int smallest = i;
			for(int j=i+1; j < input.size(); j++)
			{
				if(input.get(j).compareTo(input.get(smallest)) < 0)
				{
					smallest =j;
				}
			}
			T tmp = input.get(i);
			input.set(i, input.get(smallest));
			input.set(smallest,tmp);
		}
		return null;
	}
}
