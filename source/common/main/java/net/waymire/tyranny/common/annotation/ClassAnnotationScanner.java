package net.waymire.tyranny.common.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.waymire.tyranny.common.annotation.AnnotationScanResult.ResultType;
import net.waymire.tyranny.common.util.ClassUtil;

public class ClassAnnotationScanner implements AnnotationScanner<Class<?>>
{

	/**
	 * Scan the specified list of files for the specified annotation.
	 * Checks will include the Class definition, all methods and all members.
	 */
	@Override
	public List<AnnotationScanResult> scan(List<Class<?>> classes,String annotation)
	{
		Map<String, List<AnnotationScanResult>> results = doScan(classes,annotation);
		return results.containsKey(annotation) ? results.get(annotation) : new ArrayList<AnnotationScanResult>();
	}
	
	/**
	 * Scan the specified list of files for any annotations.
	 * Checks will include the Class definition, all methods and all members.
	 */
	@Override
	public Map<String, List<AnnotationScanResult>> scan(List<Class<?>> classes) {
		return doScan(classes,null);
	}

	private Map<String, List<AnnotationScanResult>> doScan(List<Class<?>> classes,String annotation) {
		final Map<String,List<AnnotationScanResult>> results = new HashMap<String,List<AnnotationScanResult>>();
		for(Class<?> clazz : classes)
		{
			scanClassAnnotations(results, clazz, annotation);
			scanFieldAnnotations(results, clazz, annotation);
			scanMethodAnnotations(results, clazz, annotation);
		}
		return results;
	}

	private void scanClassAnnotations(Map<String,List<AnnotationScanResult>> results,Class<?> clazz,String expression)
	{
		List<Annotation> annotations = ClassUtil.getAnnotations(clazz);
		for(Annotation annotation : annotations)
		{
			if(checkExpression(annotation, expression))
			{
				AnnotationScanResult result = new AnnotationScanResult(clazz.getName(), ResultType.CLASS, null, null);
				processAnnotationScanResult(annotation, result, results);
			}
		}
	}
	
	private void scanFieldAnnotations(Map<String,List<AnnotationScanResult>> results,Class<?> clazz,String expression)
	{
		List<Field> fields = ClassUtil.getAllClassFields(clazz);
		for(Field field : fields)
		{
			Annotation[] annotations = field.getAnnotations();
			for(Annotation annotation : annotations)
			{
				if(checkExpression(annotation, expression))
				{
					AnnotationScanResult result = new AnnotationScanResult(clazz.getName(), ResultType.FIELD, field.getName(), null);
					processAnnotationScanResult(annotation, result, results);
				}
			}
		}
	}

	private void scanMethodAnnotations(Map<String,List<AnnotationScanResult>> results,Class<?> clazz,String expression)
	{
		Method[] methods = ClassUtil.getAllClassMethods(clazz);
		for(Method method : methods)
		{
			Annotation[] annotations = method.getAnnotations();
			for(Annotation annotation : annotations)
			{
				if(checkExpression(annotation, expression))
				{
					AnnotationScanResult result = new AnnotationScanResult(clazz.getName(), ResultType.METHOD, method.getName(), null);
					processAnnotationScanResult(annotation, result, results);
				}
			}
		}
	}
	
	private void processAnnotationScanResult(Annotation annotation, AnnotationScanResult result, Map<String,List<AnnotationScanResult>> results)
	{
		String key = annotation.getClass().getName();
		if(!results.containsKey(key))
		{
			results.put(key, new ArrayList<AnnotationScanResult>());
		}
		results.get(key).add(result);
	}
	
	private boolean checkExpression(Annotation ann, String expression)
	{
		if((expression != null && expression.length() > 0))
		{
			return (ann.getClass().getName().equals(expression));
		}
		return true;
	}
}
