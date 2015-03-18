package net.waymire.tyranny.common.classloader;

import java.util.SortedSet;
import java.util.TreeSet;

import net.waymire.tyranny.common.comparator.DescendingStringLengthComparator;

/**
 * This class loader is used to filter the classes that will be loaded from a particular
 * class loader chain based on a list of package or class names and a filter type.
 * 
 * If the filter type is set to ALLOW then any package or class names in the list will be
 * attempted to be loaded upon request. All others will be ignored.
 * 
 * If the filter type is set to DENY then any requests to load a class that matches an
 * entry in the list will be ignored. All others requests will result in an attempt to 
 * locate and load the class.
 * 
 *
 */
public class FilteringClassLoader extends CustomClassLoader 
{
	public static enum FilterType { ALLOW, DENY };
	private final SortedSet<String> filters = new TreeSet<String>(new DescendingStringLengthComparator());
	private FilterType filterType = FilterType.ALLOW;
	
	public FilteringClassLoader()
	{
		this(FilteringClassLoader.class.getClassLoader());
	}
	
	public FilteringClassLoader(ClassLoader parent)
	{
		super(parent);
	}

	public void setFilterType(FilterType type)
	{
		this.filterType = type;
	}
	
	public void addFilter(String className)
	{
		filters.add(className);
	}
	
	public boolean isFiltered(String className)
	{
		return filters.contains(className);
	}
	
	@Override
	protected Class<?> loadClass(String name,boolean resolve) throws ClassNotFoundException
	{
		switch(filterType)
		{
			case ALLOW:
			{
				for(String filter : filters)
				{
					if((filter != null) && !filter.isEmpty())
					{
						if(name.startsWith(filter))
						{
							return getParent().loadClass(name);
						}
					}
				}
				return null;
			}
			case DENY:
			{
				for(String filter : filters)
				{
					if((filter != null) && !filter.isEmpty())
					{
						if(name.startsWith(filter))
						{
							return null;
						}
					}
				}
				return getParent().loadClass(name);
			}
		}
		
		return null;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		throw new ClassNotFoundException(name);
	}
}
