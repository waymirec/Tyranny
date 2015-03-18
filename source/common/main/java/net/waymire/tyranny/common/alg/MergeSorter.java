package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;

public class MergeSorter<T extends Comparable<T>> implements Sorter<T> 
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
		
		MergeSorter<Integer> sorter = new MergeSorter<Integer>();
		sorter.sort(list);
		
		for(Integer i : list) { System.out.print(i + ","); }
		System.out.println();
	}
	
	public List<T> sort(List<T> list)
	{
		List<T> sorted = this.mergeSort(list);
		list.clear();
		list.addAll(sorted);
		return sorted;
	}
	
	private List<T> mergeSort(List<T> list)
	{
		if(list.size() == 1)
		{
			return list;
		}
		List<T> left = mergeSort(list.subList(0, (list.size()/2)));
		List<T> right = mergeSort(list.subList((list.size()/2), (list.size())));
		return merge(new ArrayList<T>(left),new ArrayList<T>(right));
	}
	
	private List<T> merge(List<T> left,List<T> right)
	{
		List<T> result = new ArrayList<T>();
		while((left.size() > 0) || (right.size() > 0))
		{
			if((left.size() > 0) && (right.size() > 0))
			{
				if(left.get(0).compareTo(right.get(0)) >= 0)
				{
					result.add(right.remove(0));
				}
				else
				{
					result.add(left.remove(0));
				}
			}
			else
			{
				result.addAll(((left.size() > 0) ? left : right));
				left.clear();
				right.clear();
			}
		}
		return result;
	}	
}
