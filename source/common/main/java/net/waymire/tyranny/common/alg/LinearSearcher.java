package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;

import net.waymire.tyranny.common.GUID;

public class LinearSearcher<T extends Comparable<T>> implements Searcher<T> {
	
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
		
		Searcher<String> searcher = new LinearSearcher<String>();
		
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
			for(int i=0; i<list.size(); i++)
			{
				if(target.compareTo(list.get(i)) == 0)
				{
					return i;
				}
			}
		}
		return -1;
	}
}
