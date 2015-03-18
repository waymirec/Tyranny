package net.waymire.tyranny.instrumentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

public class Util 
{
	private Util() { }
	
	public static List<String> getMethodNamesWithAnnotation(CtClass targetClass, String annotationClassName) throws Exception
	{
		List<CtMethod> methods = getMethodsWithAnnotation(targetClass, annotationClassName);
		List<String> members = new ArrayList<String>();
		for(CtMethod method : methods)
		{
			members.add(method.getName());
		}
		return members;
	}
	
	public static List<CtMethod> getMethodsWithAnnotation(CtClass targetClass, String annotationClassName) throws Exception
	{
		List<CtMethod> ret = new ArrayList<>();
		CtMethod[] methods = getAllClassMethods(targetClass);
		
		for(CtMethod m : methods)
		{
			MethodInfo info = m.getMethodInfo();
			AnnotationsAttribute visible = (AnnotationsAttribute)info.getAttribute(AnnotationsAttribute.visibleTag);
			if(visible != null)
			{
				for(Annotation ann : visible.getAnnotations())
				{
					if(ann.getTypeName().equals(annotationClassName))
					{
						ret.add(m);
					}
				}
			}
		}
		return ret;
	}
	
	public static CtMethod[] getAllClassMethods(CtClass targetClass) throws Exception
	{
		List<CtMethod> methods = new ArrayList<>();		
		for(CtClass c = targetClass; c != null; c = c.getSuperclass())
		{
			CtMethod[] methodArray = c.getMethods();
			methods.addAll(Arrays.asList(methodArray));
		}
		return methods.toArray(new CtMethod[0]);
	}
	
	public static List<String> getFieldNamesWithAnnotation(CtClass targetClass, String annotationClassName) throws Exception
	{
		List<CtField> fields = getFieldsWithAnnotation(targetClass, annotationClassName);
		List<String> members = new ArrayList<String>();
		for(CtField field : fields)
		{
			members.add(field.getName());
		}
		return members;
	}
	
	public static List<CtField> getFieldsWithAnnotation(CtClass targetClass, String annotationClassName) throws Exception
	{
		List<CtField> ret = new ArrayList<>();
		List<CtField> fields = getAllClassFields(targetClass);
		for(CtField f : fields)
		{
			FieldInfo info = f.getFieldInfo();
			AnnotationsAttribute visible = (AnnotationsAttribute)info.getAttribute(AnnotationsAttribute.visibleTag);
			if(visible !=  null)
			{
				for(Annotation ann : visible.getAnnotations())
				{
					if(ann.getTypeName().equals(annotationClassName))
					{
						ret.add(f);
					}
				}
			}
		}
		return ret;
	}

	public static List<CtField> getAllClassFields(CtClass targetClass) throws Exception
	{
		List<CtField> fields = new ArrayList<>();
		for(CtClass c = targetClass; c != null; c = c.getSuperclass())
		{
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		return fields;
	}

}
