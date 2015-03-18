package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;

/**
 * Order of growth is theta of n-squared
 * 
 * @param <T>
 */
public class InsertionSorter<T extends Comparable<T>> implements Sorter<T> {

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
		
		Sorter<Integer> sorter = new InsertionSorter<Integer>();
		sorter.sort(list);
		
		for(Integer i : list) { System.out.print(i + ","); }
		System.out.println();
	}
	
	public List<T> sort(List<T> list)
	{
		if(list.size() <= 1)
			return list;
		
		for(int j=1; j<list.size(); j++)
		{
			T key = list.get(j);
			int i = j-1;
			while ((i >= 0) && (list.get(i).compareTo(key)) <= 0)
			{
				list.set(i+1,list.get(i));
				i = i-1;
			}
			list.set(i+1, key);
		}
		
		return list;
	}	
}
