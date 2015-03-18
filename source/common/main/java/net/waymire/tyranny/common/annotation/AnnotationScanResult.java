package net.waymire.tyranny.common.annotation;


import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class AnnotationScanResult {
	public static enum ResultType { CLASS, METHOD, FIELD };
	private final String className;
	private final ResultType type;
	private final String targetName;
	private final Map<String,Object> members;
	
	public AnnotationScanResult(String className,ResultType type,String targetName,Map<String,Object> members)
	{
		this.className = className;
		this.type = type;
		this.targetName = targetName;
		this.members = members;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public ResultType getType()
	{
		return type;
	}
	
	public String getTargetName()
	{
		return targetName;
	}
	
	public Object getMember(String name)
	{
		return members.get(name);
	}
	
	public Set<String> getMemberNames()
	{
		return members.keySet();
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
