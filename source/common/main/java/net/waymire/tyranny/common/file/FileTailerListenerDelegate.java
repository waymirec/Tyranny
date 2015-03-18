package net.waymire.tyranny.common.file;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.delegate.DelegateImpl;

public class FileTailerListenerDelegate extends DelegateImpl implements FileTailerListener
{
	private static final Class<?>[] argTypes = { List.class };

	public FileTailerListenerDelegate(Object source,String methodName) 
	{
		super(source,methodName,argTypes);
	}
	
	public FileTailerListenerDelegate(Object source, Method method)
	{
		super(source,method,argTypes);
	}
	
	@Override
	public void onFileTailRead(List<String> lines)
	{
		invoke(lines);
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
