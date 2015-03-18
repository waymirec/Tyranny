package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree<T extends Comparable<T>>
{
	private T value;
	private BinaryTree<T> left;
	private BinaryTree<T> right;
	
	public static void main(String[] args)
	{
		BinaryTree<Integer> bt = new BinaryTree<Integer>();
		bt.add(9);
		bt.add(15);
		bt.add(1);
		bt.add(5);
		bt.add(19);
		bt.add(17);
		bt.add(7);
		bt.add(3);
		bt.add(11);
		bt.add(13);
		
		BinaryTree<Integer> bt2 = bt.copy();
		System.out.println(bt2.toString());
		
		System.out.println(">>" + bt.contains(13));
		
		for(Integer i : bt.sort())
		{
			System.out.println(">" + i);
		}
		
		bt.delete(13);
		System.out.println(bt.toString());
	}
	
	public BinaryTree()
	{
	}
	
	public BinaryTree(T root)
	{
		this.value = root;
	}
	
	public void setValue(T value)
	{
		this.value = value;
	}
	
	public T getValue()
	{
		return value;
	}
	
	protected void setLeft(BinaryTree<T> left)
	{
		this.left = left;
	}
	
	protected BinaryTree<T> getLeft()
	{
		return left;
	}
	
	protected void setRight(BinaryTree<T> right)
	{
		this.right = right;
	}
	
	protected BinaryTree<T> getRight()
	{
		return right;
	}
	
	public void add(T value)
	{
		if(this.value == null)
		{
			this.value = value;
			return;
		}
		this.add(new BinaryTree<T>(value));
		
	}
	
	public void add(List<T> list)
	{
		for(T t : list)
		{
			this.add(t);
		}
	}
	
	public void delete(T value)
	{
		this.delete(null,this,value);
	}
	
	public BinaryTree<T> copy()
	{
		BinaryTree<T> target = new BinaryTree<T>();
		copy(target,this);
		return target;
	}
	
	public void copy(BinaryTree<T> target,BinaryTree<T> source)
	{
		target.setValue(source.getValue());
		if(source.getLeft() != null)
		{
			if(target.getLeft() == null)
			{
				target.setLeft(new BinaryTree<T>());
			}
			copy(target.getLeft(),source.getLeft());
		}
		
		if(source.getRight() != null)
		{
			if(target.getRight() == null)
			{
				target.setRight(new BinaryTree<T>());
			}
			copy(target.getRight(),source.getRight());
		}
	}
	
	public boolean contains(T value)
	{
		return search(this,value);
	}
	
	public List<T> sort()
	{
		List<T> list = new ArrayList<T>(size());
		sort(list,this);
		return list;
	}
	
	public int size()
	{
		return count(this);
	}
	
	public List<T> toList()
	{
		List<T> result =  new ArrayList<T>();
		result.add(value);
		if(left != null)
			result.addAll(left.toList());
		if(right != null)
			result.addAll(right.toList());
		return result;
	}
	
	private void sort(List<T> list,BinaryTree<T> root)
	{
		if(root.getLeft() != null)
		{
			sort(list,root.getLeft());
			list.add(root.getValue());
		}
		else
		{
			list.add(root.getValue());
		}
		
		if(root.getRight() != null)
		{
			sort(list,root.getRight());
		}
		
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb =  new StringBuilder();
		sb.append("[V:").append(value.toString());
		if(left != null)
			sb.append("[L:").append(left.toString()).append("]");
		if(right != null)
			sb.append("[R:").append(right.toString()).append("]");
		sb.append("]");
		return sb.toString();
	}
	
	private void add(BinaryTree<T> node)
	{
		if(node == null)
		{
			return;
		}
		
		if(this.getValue().compareTo(node.getValue()) > 0)
		{
			if(this.getLeft() == null)
			{
				this.setLeft(node);
			}
			else
			{
				this.getLeft().add(node);
			}
		}
		else
		{
			if(this.getRight() == null)
			{
				this.setRight(node);
			}
			else
			{
				this.getRight().add(node);
			}
		}
	}
		
	private boolean search(BinaryTree<T> root,T value)
	{
		if(root == null)
		{
			return false;
		}
		if(root.getValue().compareTo(value) == 0)
		{
			return true;
		}
		if(root.getValue().compareTo(value) > 0)
		{
			return search(root.getLeft(),value);
		}
		else
		{
			return search(root.getRight(),value);
		}
	}
	
	private void delete(BinaryTree<T> parent,BinaryTree<T> target, T value)
	{
		if(target == null)
		{
			return;
		}
		if(target.getValue().compareTo(value) == 0)
		{
			if(parent.getValue().compareTo(target.getValue()) > 0)
			{
				parent.setLeft(null);
			}
			else
			{
				parent.setRight(null);
			}
			parent.add(target.getLeft());
			parent.add(target.getRight());
			return;
		}
		if(target.getValue().compareTo(value) > 0)
		{
			delete(target,target.getLeft(),value);
		}
		else
		{
			delete(target,target.getRight(),value);
		}
	}
	
	private int count(BinaryTree<T> root)
	{
		int count = 0;
		if(root != null)
		{
			count += count(root.getLeft());
			count += count(root.getRight());
		}
		return count;
	}
}