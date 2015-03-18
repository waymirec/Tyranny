package net.waymire.tyranny.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * FixedCapacityList is a type of List that has both a soft and hard
 * fixed capacity. All of the elements above the soft capacity are 
 * effectively unavailable. Once the hard capacity is reached the list
 * is truncated back down to the soft capacity size.
 *
 * @param <E>			the type of elements in this List
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 */
public class FixedCapacityList<E> implements List<E>
{
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final int softCapacity;
    private final int hardCapacity;
    
    private final List<E> internal = new ArrayList<E>();
     
    public FixedCapacityList(int softCapacity)
    {
    	this(softCapacity, (int)Math.round(softCapacity * 1.25));
    }
    
    public FixedCapacityList(int softCapacity, int hardCapacity)
    {
    	this.softCapacity = softCapacity;
    	this.hardCapacity = hardCapacity;
    }
    
    @Override
    /**
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean add(E element)
    {
    	lock.writeLock().lock();
    	try
    	{
    		boolean result = internal.add(element);
    		truncate(false);
    		return result;
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#add(int, java.lang.Object)
     */
    public void add(int index, E element)
    {
    	lock.writeLock().lock();
    	try
    	{
    		internal.add(index, element);
    		truncate(false);
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends E> c)
    {
    	lock.writeLock().lock();
    	try
    	{
    		boolean result = internal.addAll(c);
    		truncate(false);
    		return result;
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection<? extends E> c)
    {
    	lock.writeLock().lock();
    	try
    	{
    		boolean result = internal.addAll(index,c);
    		truncate(false);
    		return result;
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#clear()
     */
    public void clear()
    {
    	lock.writeLock().lock();
    	try
    	{
    		internal.clear();
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object o)
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().contains(o);
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c)
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().containsAll(c);
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().equals(o);
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#get(int)
     */
    public E get(int index)
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().get(index);
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().hashCode();
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o)
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().indexOf(o);
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty()
    {
    	lock.readLock().lock();
    	try
    	{
    		return internal.isEmpty();
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#iterator()
     */
    public Iterator<E> iterator()
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().iterator();
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object o)
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().lastIndexOf(o);
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#listIterator()
     */
    public ListIterator<E> listIterator()
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().listIterator();
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#listIterator(int)
     */
    public ListIterator<E> listIterator(int index)
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().listIterator(index);
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#remove(int)
     */
    public E remove(int index)
    {
    	lock.writeLock().lock();
    	try
    	{
    		truncate(true);
    		return getData().remove(index);
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object object)
    {
    	lock.writeLock().lock();
    	try
    	{
    		truncate(true);
    		return getData().remove(object);
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c)
    {
    	lock.writeLock().lock();
    	try
    	{
    		truncate(true);
    		return getData().removeAll(c);
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c)
    {
    	lock.writeLock().lock();
    	try
    	{
    		truncate(true);
    		return internal.retainAll(c);
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#set(int, java.lang.Object)
     */
    public E set(int index, E element)
    {
    	lock.writeLock().lock();
    	try
    	{
    		truncate(true);
    		return getData().set(index,element);
    	}
    	finally
    	{
    		lock.writeLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#size()
     */
    public int size()
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().size();
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#subList(int, int)
     */
    public List<E> subList(int fromIndex, int toIndex)
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().subList(fromIndex, toIndex);
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#toArray()
     */
    public Object[] toArray()
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().toArray();
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    @Override
    /**
     * @see java.util.List#toArray(T[])
     */
    public <T> T[] toArray(T[] a)
    {
    	lock.readLock().lock();
    	try
    	{
    		return getData().toArray(a);
    	}
    	finally
    	{
    		lock.readLock().unlock();
    	}
    }
    
    /**
     * Truncate the List.
     * 
     * This method will reduce the size of the List so that it
     * is no larger then the configured soft capacity.
     * 
     * If called with a <code>force</code> of false then the
     * list is only truncated if its size is over the hard
     * capacity.
     * 
     * @param force			whether to force the truncate
     */
    private void truncate(boolean force)
    {
        lock.writeLock().lock();
        try
        {
        	if(force || internal.size() >= hardCapacity)
        	{
        		int count = (int)(internal.size() - softCapacity);
        		for(int i=0; i<count; i++)
        		{
        			internal.remove(i);
        		}
        	}
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Returns the list data that is within the soft capacity.
     * 
     * @return			list data
     */
    private List<E> getData()
    {
    	if(internal.size() <= softCapacity)
		{
    		return internal;
		}
		else
		{
			int end = internal.size() - 1;
			int start = end - softCapacity;
			return internal.subList(start, end);
		}
    }
}
