package net.waymire.tyranny.instrumentation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class TraceTransformer implements ClassFileTransformer 
{
	
	private static final String TRACE_CLASSNAME = "net.waymire.tyranny.common.annotation.Trace";
	private static final String LOGGER_CLASSNAME = "java.util.logging.Logger";
	private static final String LOGGER_FIELD_CLASSNAME = "net.waymire.tyranny.common.annotation.LoggerField";
	private static final String DEFAULT_LOGGER_MEMBER_NAME = "__auto_gen_logger__";
	
	private ClassLoader customClassLoader = null;
	private ClassPool classPool = null;
	
	public TraceTransformer()
	{
		this.customClassLoader = null;
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> targetClass, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
	{
		if(className.startsWith("net/waymire"))
		{
			if(customClassLoader == null)
			{
				synchronized(this)
				{
					if(customClassLoader == null)
					{
						initClassLoader(loader);
					}
				}
			}

			byte[] bytes = classfileBuffer;
			
			try
			{
				String properClassName = className.replaceAll("/", ".");
				
				CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

				List<CtMethod> traceMethods = Util.getMethodsWithAnnotation(ctClass, TRACE_CLASSNAME);
				
				if(!traceMethods.isEmpty())
				{
					for(CtMethod method : traceMethods)
					{
						MethodInfo info = method.getMethodInfo();
						AnnotationsAttribute visible = (AnnotationsAttribute)info.getAttribute(AnnotationsAttribute.visibleTag);
						if(visible != null)
						{
							Annotation ann = visible.getAnnotation(TRACE_CLASSNAME); 
							String logger = null;
							Object annLoggerMemberValue = ann.getMemberValue("logger");
							if(annLoggerMemberValue != null && annLoggerMemberValue.toString().length() > 0)
							{
								logger = annLoggerMemberValue.toString().replaceAll("\"", "");
								if(ctClass.getField(logger) == null)
								{
									log("Error Transforming [%s.%s()]. Logger Field [%s] Does Not Exist.", properClassName, method.getName(), logger);
									logger = null;
								}
								else
								{
									log("Transforming [%s.%s()] With Logger Field [%s]", properClassName, method.getName(), logger);
								}
							}
							
							if(logger == null)
							{
								List<String> loggers = Util.getFieldNamesWithAnnotation(ctClass, LOGGER_FIELD_CLASSNAME);
								if(!loggers.isEmpty())
								{
									logger = loggers.get(0);
									log("Transforming [%s.%s()] With Logger Field [%s]", properClassName, method.getName(), logger);
								}
								else
								{
									try
									{
										CtField loggerField = ctClass.getField(DEFAULT_LOGGER_MEMBER_NAME);
										logger = loggerField.getName();
										log("Transforming [%s.%s()] With Previously Auto Generated Logger Field [%s]", properClassName, method.getName(), logger);
									}
									catch(javassist.NotFoundException notFoundException)
									{
										ClassFile ccFile = ctClass.getClassFile();
										ConstPool constPool = ccFile.getConstPool();
									
										AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
										Annotation annot = new Annotation("net.waymire.tyranny.common.annotation.LoggerField",constPool);
										attr.addAnnotation(annot);

										String def = String.format("private static %s %s;", LOGGER_CLASSNAME,DEFAULT_LOGGER_MEMBER_NAME);
										CtField loggerField = CtField.make(def, ctClass);
								        String getLogger = "java.util.logging.Logger.getLogger(" + className.replace('/', '.') + ".class.getName());";
								        ctClass.addField(loggerField, getLogger);

										log("Transforming [%s.%s()] With Auto Generated Logger Field [%s]", properClassName, method.getName(), loggerField.getName());
										logger = loggerField.getName();
									}
								}
							}

							transformMethod(method, logger);
						}
					}					
				}

				bytes = ctClass.toBytecode();
				ctClass.detach();
				
				return bytes;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		return classfileBuffer;
	}

	private void transformMethod(final CtMethod method, final String logger) throws Exception
	{
		try
		{
			ExprEditor e = new ExprEditor()
			{
				public void edit(MethodCall m) throws CannotCompileException
				{
					String entering = String.format("Executing Method: %s.%s(%s)", m.getClassName(),m.getMethodName(), m.getSignature());
					String exiting = String.format("Finished executing Method: %s.%s(%s)", m.getClassName(),m.getMethodName(), m.getSignature());

					StringBuffer body = new StringBuffer();
					body.append(String.format("{ try { if(%s.isLoggable(java.util.logging.Level.FINER)) %s.log(java.util.logging.Level.FINER, \"%s\"); ", logger, logger, entering ));
					body.append("$_ = $proceed($$);");
					body.append(String.format("} finally { if(%s.isLoggable(java.util.logging.Level.FINER)) %s.log(java.util.logging.Level.FINER, \"%s\"); }}", logger, logger, exiting ));
					m.replace(body.toString());
				}
			};
			method.instrument(e);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		String entering = String.format("Entering Method: %s.%s(%s)", method.getDeclaringClass().getName(),method.getName(), method.getSignature());
		String exiting = String.format("Exiting Method: %s.%s(%s)", method.getDeclaringClass().getName(),method.getName(), method.getSignature());
		method.insertBefore(String.format("{ if(%s.isLoggable(java.util.logging.Level.FINER)) %s.log(java.util.logging.Level.FINER, \"%s\"); }", logger, logger, entering ));
		method.insertAfter(String.format("{ if(%s.isLoggable(java.util.logging.Level.FINER)) %s.log(java.util.logging.Level.FINER, \"%s\"); }", logger, logger, exiting ), true);
	}
	
	/*
	private void transformMethod(final CtMethod method, final String logger) throws Exception
	{
		String entering = String.format("Entering Method: %s.%s(%s)", method.getDeclaringClass().getName(),method.getName(), method.getSignature());
		String exiting = String.format("Exiting Method: %s.%s(%s)", method.getDeclaringClass().getName(),method.getName(), method.getSignature());
		String origMethodName = method.getName();

		if(!method.getName().endsWith("$impl"))
		{
			method.setName(method.getName()+"$impl");
		}

        CtMethod newMethod = CtNewMethod.copy(method, origMethodName, method.getDeclaringClass(), null);
        String returnType = method.getReturnType().getName();

		StringBuffer body = new StringBuffer();
		body.append(String.format("{ try { if(%s.isLoggable(java.util.logging.Level.FINER)) %s.log(java.util.logging.Level.FINER, \"%s\"); ", logger, logger, entering ));
		if (!"void".equals(method.getReturnType().getName())) 
		{
            body.append(returnType + " result = ");
        }
		
        body.append(method.getName() + "($$);\n");
        
        if (!"void".equals(returnType)) 
        {
            body.append("return result;\n");
        }
        
        body.append(String.format("} finally { if(%s.isLoggable(java.util.logging.Level.FINER)) %s.log(java.util.logging.Level.FINER, \"%s\"); }}", logger, logger, exiting ));

        newMethod.setBody(body.toString());
        method.getDeclaringClass().addMethod(newMethod);
        
        try
		{
			ExprEditor e = new ExprEditor()
			{
				public void edit(MethodCall m) throws CannotCompileException
				{
					String entering = String.format("Executing Method: %s.%s(%s)", m.getClassName(),m.getMethodName(), m.getSignature());
					String exiting = String.format("Finished executing Method: %s.%s(%s)", m.getClassName(),m.getMethodName(), m.getSignature());

					StringBuffer body = new StringBuffer();
					body.append(String.format("{ try { if(%s.isLoggable(java.util.logging.Level.FINER)) %s.log(java.util.logging.Level.FINER, \"%s\"); ", logger, logger, entering ));
					body.append("$_ = $proceed($$);");
					body.append(String.format("} finally { if(%s.isLoggable(java.util.logging.Level.FINER)) %s.log(java.util.logging.Level.FINER, \"%s\"); }}", logger, logger, exiting ));
					m.replace(body.toString());
				}
			};
			method.instrument(e);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	*/
	
	private ClassLoader initClassLoader(ClassLoader parent)
	{
		File self = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		File parentFile = self.getParentFile();
		List<File> files = Arrays.asList(new File(parentFile.getAbsolutePath()).listFiles());

		customClassLoader = new FileClassLoader(files, parent);
		classPool = ClassPool.getDefault();
		classPool.insertClassPath(new LoaderClassPath(customClassLoader));
		return customClassLoader;
	}
	
	private void log(String format, Object...args)
	{
		String message = String.format(format,args);
		System.out.printf("[%s] %s\n", this.getClass().getName(), message);
	}
}
