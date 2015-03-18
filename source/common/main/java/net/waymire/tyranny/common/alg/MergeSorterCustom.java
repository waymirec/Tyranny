package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;

/**
 * Conceptually, a merge sort works as follows
 * 
 * Divide the unsorted list into n sublists, each containing 1 element 
 * (a list of 1 element is considered sorted). 
 * 
 * Repeatedly merge sublists to produce new sublists until there is only 1 
 * sublist remaining. This will be the sorted list.
 */
public class MergeSorterCustom<T extends Comparable<T>> implements Sorter<T> 
{
	public static void main(String[] args)
	{
		List<Integer> list = new ArrayList<Integer>();
		list.add(9);
		list.add(15);
		list.add(1);
		list.add(5);
		list.add(19);
		list.add(17);
		list.add(7);
		list.add(3);
		list.add(11);
		list.add(13);
		list.add(15);
		list.add(20);
		list.add(2);
		
		Sorter<Integer> sorter = new MergeSorterCustom<Integer>();
		sorter.sort(list);
		
		for(Integer i : list) { System.out.print(i + ","); }
		System.out.println();
	}
	
	public List<T> sort(List<T> list)
	{
		for(int i=1; i < list.size(); i = i*2)
		{
			for(int j=0; j <= list.size() - i; j = j + (2*i))
			{
				merge(list, j, i, Math.min(i,  (list.size() - j - i)));
			}
		}
		return list;
	}
	
	private void merge(List<T> list, int start1, int len1, int len2)
	{
		int end1 = (start1 + len1)-1;
		int end2 = end1 + len2;
		
		int i = start1; 		// index of compared value in first array
		int j = start1 + len1; 	// index of compared value in second array
		int k = 0;				// sorted array index to place value
		
		@SuppressWarnings("unchecked")
		T[] t = (T[]) new Comparable[len1+len2];
		while((i <= end1) && (j <= end2))
		{
			if(list.get(i).compareTo(list.get(j)) <= 0)
			{
				t[k] = list.get(i);
				++i;
				++k;
			}
			else
			{
				t[k] = list.get(j);
				++j;
				++k;
			}
		}
		while(i <= end1)
		{
			t[k] = list.get(i);
			++i;
			++k;
		}
		while(j <= end2)
		{
			t[k] = list.get(j);
			++j;
			++k;
		}
		
		for(int y=start1,z=0; z<=t.length-1; z++,y++)
		{
			list.set(y, t[z]);
		}
	}	
}
