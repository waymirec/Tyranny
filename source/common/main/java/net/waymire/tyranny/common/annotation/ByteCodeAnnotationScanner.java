package net.waymire.tyranny.common.annotation;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.waymire.tyranny.common.annotation.AnnotationScanResult.ResultType;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.util.ExceptionUtil;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class ByteCodeAnnotationScanner implements AnnotationScanner<File>
{
	private static final String JAR_EXT = ".JAR";
	private static final String CLASS_EXT = ".CLASS";
	
	/**
	 * Scan the specified list of files for the specified annotation.
	 * Checks will include the Class definition, all methods and all members.
	 */
	@Override
	public List<AnnotationScanResult> scan(List<File> files,String annotation)
	{
		Map<String, List<AnnotationScanResult>> results = doScan(files,annotation);
		return results.containsKey(annotation) ? results.get(annotation) : new ArrayList<AnnotationScanResult>();
	}
	
	/**
	 * Scan the specified list of files for any annotations.
	 * Checks will include the Class definition, all methods and all members.
	 */
	@Override
	public Map<String, List<AnnotationScanResult>> scan(List<File> files) {
		return doScan(files,null);
	}

	private Map<String, List<AnnotationScanResult>> doScan(List<File> files,String annotation) {
		final Map<String,List<AnnotationScanResult>> results = new HashMap<String,List<AnnotationScanResult>>();
		for(File file : files)
		{
			if(file.exists() && file.isFile())
			{
				if(file.getName().toUpperCase().endsWith(JAR_EXT))
				{
					try
					{
						JarFile jar = new JarFile(file);
						Enumeration<JarEntry> entries = jar.entries();
						while(entries.hasMoreElements())
						{
							JarEntry entry = entries.nextElement();
							if(entry.getName().toUpperCase().endsWith(CLASS_EXT))
							{
								InputStream is = jar.getInputStream(entry);
								DataInputStream dstream = new DataInputStream(new BufferedInputStream(is));

								ClassFile cf =  new ClassFile(dstream);
								scanClassAnnotations(results,cf,annotation);
								scanFieldAnnotations(results,cf,annotation);
								scanMethodAnnotations(results,cf,annotation);
							}
						}
						jar.close();
					}
					catch(Exception e)
					{
						LogHelper.severe(this, "Exception Scanning For Annotation [{0}]: {1}", annotation, ExceptionUtil.getStackTrace(e));
					}
				}
				else if(file.getName().toUpperCase().endsWith(CLASS_EXT))
				{
					try
					{
						InputStream is = new FileInputStream(file);
						DataInputStream dstream = new DataInputStream(new BufferedInputStream(is));
						ClassFile cf =  new ClassFile(dstream);
						scanClassAnnotations(results,cf,annotation);
						scanFieldAnnotations(results,cf,annotation);
						scanMethodAnnotations(results,cf,annotation);
					}
					catch(Exception e)
					{
						LogHelper.severe(this, "Exception Scanning For Annotation [{0}]: {1}", annotation, ExceptionUtil.getStackTrace(e));
					}
				}
			}
		}
		return results;
	}

	private void scanClassAnnotations(Map<String,List<AnnotationScanResult>> results,ClassFile cf,String expression)
	{
		AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag);
		if(visible != null)
		{
			for (Annotation ann : visible.getAnnotations())
			{
				if(!ann.getTypeName().startsWith("java.lang."))
				{					
					if(checkExpression(ann,expression))
					{
						Map<String,Object> members = processAnnotationMembers(ann);
						AnnotationScanResult result = new AnnotationScanResult(cf.getName(),ResultType.CLASS,cf.getName(),members);
						if(!results.containsKey(ann.getTypeName()))
						{
							results.put(ann.getTypeName(),new ArrayList<AnnotationScanResult>());
						}
						results.get(ann.getTypeName()).add(result);
					}
				}
			}
		}
	}
	
	private void scanFieldAnnotations(Map<String,List<AnnotationScanResult>> results,ClassFile cf,String expression)
	{
		for(Object o : cf.getFields())
		{
			FieldInfo info = (FieldInfo)o;
			AnnotationsAttribute visible = (AnnotationsAttribute)info.getAttribute(AnnotationsAttribute.visibleTag);
			if(visible !=  null)
			{
				for(Annotation ann : visible.getAnnotations())
				{
					if(!ann.getTypeName().startsWith("java.lang."))
					{
						if(checkExpression(ann,expression))
						{
							Map<String,Object> members = processAnnotationMembers(ann);
							AnnotationScanResult result = new AnnotationScanResult(cf.getName(),ResultType.FIELD,info.getName(),members);
							if(!results.containsKey(ann.getTypeName()))
							{
								results.put(ann.getTypeName(),new ArrayList<AnnotationScanResult>());
							}
							results.get(ann.getTypeName()).add(result);
						}
					}
				}
			}			
		}
	}

	private void scanMethodAnnotations(Map<String,List<AnnotationScanResult>> results,ClassFile cf,String expression)
	{
		for(Object o : cf.getMethods())
		{
			MethodInfo info = (MethodInfo)o;
			AnnotationsAttribute visible = (AnnotationsAttribute)info.getAttribute(AnnotationsAttribute.visibleTag);
			if(visible !=  null)
			{
				for(Annotation ann : visible.getAnnotations())
				{
					if(!ann.getTypeName().startsWith("java.lang."))
					{
						if(checkExpression(ann,expression))
						{
							Map<String,Object> members = processAnnotationMembers(ann);
							AnnotationScanResult result = new AnnotationScanResult(cf.getName(),ResultType.METHOD,info.getName(),members);
							if(!results.containsKey(ann.getTypeName()))
							{
								results.put(ann.getTypeName(),new ArrayList<AnnotationScanResult>());
							}
							results.get(ann.getTypeName()).add(result);
						}
					}
				}
			}
		}
	}
	
	private Map<String,Object> processAnnotationMembers(Annotation ann)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		if(ann.getMemberNames() !=  null)
		{
			for(Object key : ann.getMemberNames())
			{
				String memberName = (String)key;
				MemberValue memberValue = ann.getMemberValue(memberName);
				Object value = getAnnotationMemberValue(memberValue);				
				result.put(memberName, value);
			}
		}
		return result;
	}
	
	private Object getAnnotationMemberValue(MemberValue value)	
	{
		if     (value instanceof ArrayMemberValue)
		{
			MemberValue[] values = ((ArrayMemberValue) value).getValue();
			List<Object> ret = new ArrayList<Object>();
			
			for(MemberValue v : values)
			{
				ret.add(getAnnotationMemberValue(v));
			}
			return (Object)ret;
		}
		else if(value instanceof StringMemberValue)
		{
			return ((StringMemberValue)value).getValue();
		}
		else if(value instanceof IntegerMemberValue)
		{
			return ((IntegerMemberValue)value).getValue();
		}
		else if(value instanceof ByteMemberValue)
		{
			return ((ByteMemberValue)value).getValue();
		}
		else if(value instanceof BooleanMemberValue)
		{
			return ((BooleanMemberValue)value).getValue();
		}
		else if(value instanceof CharMemberValue)
		{
			return ((CharMemberValue)value).getValue();
		}
		else if(value instanceof ShortMemberValue)
		{
			return ((ShortMemberValue)value).getValue();
		}
		else if(value instanceof LongMemberValue)
		{
			return ((LongMemberValue)value).getValue();
		}
		else if(value instanceof DoubleMemberValue)
		{
			return ((DoubleMemberValue)value).getValue();
		}
		else if(value instanceof FloatMemberValue)
		{
			return ((FloatMemberValue)value).getValue();
		}
		else if(value instanceof ClassMemberValue)
		{
			return ((ClassMemberValue)value).getValue();
		}
		else if(value instanceof EnumMemberValue)
		{
			return ((EnumMemberValue)value).getValue();
		}
		else
		{
			return null;
		}
	}
	
	private boolean checkExpression(Annotation ann, String expression)
	{
		if((expression != null && expression.length() > 0))
		{
			return (ann.getTypeName().equals(expression));
		}
		return true;
	}
}
