package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;

/**
 * Quicksort is a divide and conquer algorithm.
 *  
 * Quicksort first divides a large list into two smaller sub-lists: the low 
 * elements and the high elements. 
 * 
 * Quicksort can then recursively sort the sub-lists.
 * 
 * The steps are:
 *		Pick an element, called a pivot, from the list.
 *
 * 		Reorder the list so that all elements with values less than the pivot 
 * 		come before the pivot, while all elements with values greater than the 
 * 		pivot come after it (equal values can go either way). After this 
 * 		partitioning, the pivot is in its final position. This is called 
 * 		the partition operation.
 * 
 *		Recursively apply the above steps to the sub-list of elements with 
 *		smaller values and separately the sub-list of elements with greater values.  
 */
public class QuickSorter<T extends Comparable<T>> implements Sorter<T> {
	
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
		
		Sorter<Integer> sorter = new QuickSorter<Integer>();
		sorter.sort(list);
				
		for(Integer i : list) { System.out.print(i + ","); }
		System.out.println();
	}
	
	public List<T> sort(List<T> list)
	{
		return quickSort(list, 0, list.size()-1);
	}
	
	private List<T> quickSort(List<T> list, int start, int stop)
	{
		// If the list has less than 2 items then there is nothing to sort
		if((stop - start) <= 0)
			return list;
		
		// Initialize the left and right pointers
		int left = start;
		int right = stop;
		
		// The pivot is the left most element
		T pivot = list.get(left);

		// While the left and right pointers are separated we 
		// continue processing elements
		while(left < right)
		{
			// If the value located at the right pointer is greater then
			// or equal to the pivot value then slide the right pointer
			// to the left
			while((left != right) && (pivot.compareTo(list.get(right)) <= 0))
			{
				right--;
			}
			
			// If the pointers match then we are done
			if(left == right) break;
			
			// The value located at the right pointer
			// is less than or equal to the pivot value
			// and should be moved over to the left side
			list.set(left, list.get(right));
			// Nullify the element at the right pointer
			list.set(right, null);
			// Move the left  pointer forward to the next element
			left++;
			
			// If the value located at the left pointer is less then
			// or equal to the pivot value then slide the left pointer
			// to the right
			while((left != right) && (pivot.compareTo(list.get(left)) >= 0))
			{
				left++;
			}
			
			// If the pointers match then we are done
			if(left == right) break;
			
			// The value located at the left pointer
			// is greater then or equal to the pivot value
			// and should be moved over to the right side
			list.set(right, list.get(left));
			// Nullify the element at the right pointer
			list.set(left, null);
			// Move the right pointer forward to the next element
			right--;
		}
		
		// If the pointers are not equal something bad happened
		if(left != right)
			throw new IllegalStateException();
		
		// Drop the pivot value into its proper place
		list.set(left, pivot);
		
		// Iterate over the elements to the left of the pivot
		quickSort(list,start,left-1);
		// Iterate over the elements to the right of the pivot
		quickSort(list, left+1,stop);
		return list;
		
	}
}
