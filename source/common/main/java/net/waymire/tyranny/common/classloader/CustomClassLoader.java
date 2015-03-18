package net.waymire.tyranny.common.classloader;

public class CustomClassLoader extends ClassLoader implements Cloneable {

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
