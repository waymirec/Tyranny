package net.waymire.tyranny.common.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

final public class FileList {
	private static enum FilterType { PREFIX, SUFFIX };
	
	private FileList() { }
	
	public static List<File> findBySuffix(final String suffix,final boolean recursive,final File ... dirs)
	{
		List<File> results = new ArrayList<File>();
		for(File dir : dirs)
		{
			List<File> files = find(dir,suffix,FilterType.SUFFIX,recursive);
			results.addAll(files);
		}
		return results;
	}
	
	public static List<File> findBySuffix(final String suffix,final File ... dirs)
	{
		return findBySuffix(suffix,false,dirs);
	}
	
	public static List<File> findByPrefix(final File dir,final String prefix)
	{
		return findByPrefix(dir,prefix,false);
	}

	public static List<File> findByPrefix(final File dir,final String prefix,final boolean recursive)
	{
		return find(dir,prefix,FilterType.PREFIX,recursive);
	}
	
	private static List<File> find(final File dir,final String value,final FilterType type,final boolean recursive)
	{
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir,String name)
			{
				if(recursive && dir.isDirectory())
				{
					return true;
				}
				
				switch(type)
				{
					case PREFIX:
						return(name.toUpperCase().startsWith(value.toUpperCase()));
					case SUFFIX:
						return(name.toUpperCase().endsWith(value.toUpperCase()));
				}
				return false;
			}
		});

		List<File> list =  new ArrayList<File>();
		if(files !=  null)
		{
			for(File file : files)
			{
				if(recursive && file.isDirectory())
				{
					list.addAll(find(file,value,type,true));
				}
				else
				{
					list.add(file);
				}
			}
		}
		return list;
	}
}
