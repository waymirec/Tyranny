package net.waymire.tyranny.common.proxy;

import java.lang.reflect.Method;
import java.util.List;

public interface VirtualBean 
{
	public void setProperty(String tag,Object obj);
	public Object getProperty(String tag);
	
	public boolean hasChanged(String tag);
	public boolean isDirty(String tag);

	public List<String> getDirtyProperties();
	public List<Method> getDirtyPropertyMethods();
}
