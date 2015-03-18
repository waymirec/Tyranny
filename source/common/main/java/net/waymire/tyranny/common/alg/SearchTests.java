package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;

import net.waymire.tyranny.common.GUID;

public class SearchTests {
	public static void main(String[] args)
	{
		List<String> list = new ArrayList<String>();
		System.out.println("Generating random strings...");
		long start = System.currentTimeMillis();
		for(int i=0; i<5000000; i++)
		{
			list.add(GUID.generate().toString());
		}
		long end = System.currentTimeMillis();
		System.out.println("Completed in " + (end - start) + "ms" + "\n");
		
		List<Searcher<String>> searchers = new ArrayList<Searcher<String>>();
		searchers.add(new LinearSearcher<String>());
		
		for(Searcher<String> searcher : searchers)
		{
			System.out.print("Searching with " + searcher.getClass().getSimpleName() + ": ");
			long delta = testSearcher(list, list.get(list.size()-1), searcher);
			System.out.println(delta + "ms");
		}
	}
	
	private static long testSearcher(List<String> in, String target, Searcher<String> searcher)
	{
		long start = System.currentTimeMillis();
		int index = searcher.find(in, target);
		long end = System.currentTimeMillis();
		long delta = end - start;
		return delta;
	}
}
