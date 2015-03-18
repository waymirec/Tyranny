package net.waymire.tyranny.instrumentation;

public class CustomClassLoader extends ClassLoader implements Cloneable
{
	public CustomClassLoader(ClassLoader parent)
	{
		super(parent);
	}
	
	@Override
	public Object clone()
	{
		return this;
	}
}
