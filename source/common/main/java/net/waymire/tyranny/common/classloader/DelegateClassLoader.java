package net.waymire.tyranny.common.classloader;

public class DelegateClassLoader extends ClassLoader
{
	private CustomClassLoader delegate;
	
	public DelegateClassLoader(CustomClassLoader delegate)
	{
		this.delegate = delegate;
	}
	
	protected ClassLoader getDelegate()
	{
		return delegate;
	}
	
	public void reload()
	{
		CustomClassLoader loader = (CustomClassLoader)delegate.clone();
		this.delegate = loader;
	}

	@Override
	protected Class<?> loadClass(final String className,boolean resolve) throws ClassNotFoundException
	{
		return delegate.loadClass(className);
	}	
	
	@Override
	public String toString()
	{
		return delegate.toString();
	}
}
