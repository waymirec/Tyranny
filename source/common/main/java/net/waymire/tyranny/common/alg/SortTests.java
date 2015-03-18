package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SortTests {

	public static void main(String[] args)
	{
		List<Integer> list = new ArrayList<Integer>();
		Random generator = new Random(System.currentTimeMillis());
		System.out.println("Generating random numbers...");
		long start = System.currentTimeMillis();
		for(int i=0; i<250000; i++)
		{
			list.add(generator.nextInt());
		}
		long end = System.currentTimeMillis();
		System.out.println("Completed in " + (end - start) + "ms" + "\n");
		
		List<Sorter<Integer>> sorters = new ArrayList<Sorter<Integer>>();
		//sorters.add(new BinaryTreeSorter<Integer>());
		//sorters.add(new MergeSorter<Integer>());
		sorters.add(new MergeSorterCustom<Integer>());
		//sorters.add(new SelectionSorter<Integer>());
		sorters.add(new QuickSorter<Integer>());
		sorters.add(new InsertionSorter<Integer>());
		//sorters.add(new BubbleSorter<Integer>());
		sorters.add(new ComparatorSorter<Integer>());
		
		for(Sorter<Integer> sorter : sorters)
		{
			System.out.print("Sorting with " + sorter.getClass().getSimpleName() + ": ");
			long delta = testSorter(list, sorter);
			System.out.println(delta + "ms");
		}
	}
	
	private static long testSorter(List<Integer> in,Sorter<Integer> sorter)
	{
		//List<Integer> list = new ArrayList<Integer>(in);
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(in);
		long start = System.currentTimeMillis();
		sorter.sort(list);
		long end = System.currentTimeMillis();
		long delta = end - start;
		return delta;
	}
}
