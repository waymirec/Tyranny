package net.waymire.tyranny.common.alg;

import java.util.List;

public class BinaryTreeSorter<T extends Comparable<T>> implements Sorter<T>
{
	public List<T> sort(List<T> list)
	{
		BinaryTree<T> bt = new BinaryTree<T>();
		bt.add(list);
		bt.sort();
		return bt.toList();
	}
}
