package net.waymire.tyranny.common.alg;

import java.util.Collections;
import java.util.List;

public class ComparatorSorter<T extends Comparable<T>> implements Sorter<T> {

	public List<T> sort(List<T> list)
	{
		Collections.sort(list);
		Collections.reverse(list);
		return list;
	}
}
