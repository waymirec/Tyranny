package net.waymire.tyranny.instrumentation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

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

public class LockingTransformer implements ClassFileTransformer
{
	private static final String LOCKED_CLASSNAME = "net.waymire.tyranny.common.annotation.Locked";
	private static final String LOCK_FIELD_CLASSNAME = "net.waymire.tyranny.common.annotation.LockField";
	private static final String READWRITE_LOCK_CLASSNAME = "java.util.concurrent.locks.ReentrantReadWriteLock";
	private static final String DEFAULT_LOCK_MEMBER_NAME = "__auto_gen_lock__";
	
	private ClassLoader customClassLoader = null;
	private ClassPool classPool = null;
	
	public LockingTransformer()
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

				List<CtMethod> lockedMethods = Util.getMethodsWithAnnotation(ctClass, LOCKED_CLASSNAME);
				
				if(!lockedMethods.isEmpty())
				{
					for(CtMethod method : lockedMethods)
					{
						MethodInfo info = method.getMethodInfo();
						AnnotationsAttribute visible = (AnnotationsAttribute)info.getAttribute(AnnotationsAttribute.visibleTag);
						if(visible != null)
						{
							Annotation ann = visible.getAnnotation(LOCKED_CLASSNAME); 
							String mode = ann.getMemberValue("mode").toString().toLowerCase();
							mode = mode.substring(mode.lastIndexOf(".")+1,mode.length());
							String lockMethod = mode + "Lock";
							String lock = null;
							Object annLockMemberValue = ann.getMemberValue("lock");
							if(annLockMemberValue != null && annLockMemberValue.toString().length() > 0)
							{
								lock = annLockMemberValue.toString().replaceAll("\"", "");
								if(ctClass.getField(lock) == null)
								{
									log("Error Transforming [%s.%s()]. Lock Field [%s] Does Not Exist.", properClassName, method.getName(), lock);
									lock = null;
								}
								else
								{
									log("Transforming [%s.%s()] With Lock Field [%s]", properClassName, method.getName(), lock);
								}
							}
							
							if(lock == null)
							{
								List<String> locks = Util.getFieldNamesWithAnnotation(ctClass, LOCK_FIELD_CLASSNAME);
								if(!locks.isEmpty())
								{
									lock = locks.get(0);
									log("Transforming [%s.%s()] With Lock Field [%s]", properClassName, method.getName(), lock);
								}
								else
								{
									try
									{
										CtField lockField = ctClass.getField(DEFAULT_LOCK_MEMBER_NAME);
										lock = lockField.getName();
										log("Transforming [%s.%s()] With Previously Auto Generated Lock Field [%s]", properClassName, method.getName(), lock);
									}
									catch(javassist.NotFoundException notFoundException)
									{
										CtClass lockFileClass = classPool.get(READWRITE_LOCK_CLASSNAME);
										ClassFile ccFile = ctClass.getClassFile();
										ConstPool constPool = ccFile.getConstPool();
									
										AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
										Annotation annot = new Annotation("net.waymire.tyranny.common.annotation.LockField",constPool);
										attr.addAnnotation(annot);

										CtField lockField = new CtField(lockFileClass, DEFAULT_LOCK_MEMBER_NAME, ctClass);
										lockField.setModifiers(Modifier.STATIC | Modifier.FINAL);
										lockField.getFieldInfo().addAttribute(attr);
									
										log("Transforming [%s.%s()] With Auto Generated Lock Field [%s]", properClassName, method.getName(), lockField.getName());
										ctClass.addField(lockField, CtField.Initializer.byNew(lockFileClass));
										lock = lockField.getName();
									}
								}
							}

							transformMethod(method, lock, lockMethod);
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

	private void transformMethod(CtMethod method, final String lock, final String lockMethod) throws Exception
	{
		method.insertBefore(String.format("{ %s.%s().lock(); }", lock, lockMethod));
		method.insertAfter(String.format("{ %s.%s().unlock(); }",lock, lockMethod), true);		
	}
	
	private ClassLoader initClassLoader(ClassLoader parent)
	{
		File self = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		File parentFile = self.getParentFile();
		List<File> files = Arrays.asList(new File(parentFile.getAbsolutePath()).listFiles());

		customClassLoader = new FileClassLoader(files, parent);
		classPool = ClassPool.getDefault();
		classPool.insertClassPath(new LoaderClassPath(customClassLoader));
		return customClassLoader;	}
	
	private void log(String format, Object...args)
	{
		String message = String.format(format,args);
		System.out.printf("[%s] %s\n", this.getClass().getName(), message);
	}
}
