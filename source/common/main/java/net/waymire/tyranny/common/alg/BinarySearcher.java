package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;

import net.waymire.tyranny.common.GUID;

public class BinarySearcher<T extends Comparable<T>>  implements Searcher<T> {
	public static void main(String[] args)
	{
		System.out.println("Generating random strings...");
		long start = System.currentTimeMillis();
		List<String> list = new ArrayList<String>();
		for(int i=0; i<900000; i++)
		{
			list.add(GUID.generate().toString());
		}
		long stop = System.currentTimeMillis();
		System.out.println("Completed in " + (stop - start) + "ms" + "\n");
		
		String target = list.get(list.size()-1);
		
		Sorter<String> sorter = new QuickSorter<String>();
		sorter.sort(list);
		
		Searcher<String> searcher = new BinarySearcher<String>();
		start = System.currentTimeMillis();
		int index = searcher.find(list,"test");
		stop = System.currentTimeMillis();

		System.out.printf("Found [%s] at index [%d] in [%d] ms", target, index, (stop - start));
		System.out.println();
	}
	
	public int find(List<T> list, T target)
	{
		if(target != null)
		{
			int low = 0;
			int high = list.size()-1;
			int test;
			while ((low + 1) < high)
			{
				test = (low + high) / 2;
				if(target.compareTo(list.get(test)) < 0)
				{
					high = test;
				}
				else
				{
					low = test;
				}
			}
			
			if(target.compareTo(list.get(low)) == 0)
			{
				return low;
			}
		}
		return -1;
	}
}
