package net.waymire.tyranny.common.protocol;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.SystemConstants;
import net.waymire.tyranny.common.annotation.AnnotationScanResult;
import net.waymire.tyranny.common.annotation.AnnotationScanner;
import net.waymire.tyranny.common.annotation.ByteCodeAnnotationScanner;
import net.waymire.tyranny.common.annotation.AnnotationScanResult.ResultType;
import net.waymire.tyranny.common.file.FileNotFoundException;
import net.waymire.tyranny.common.file.FileNotReadableException;
import net.waymire.tyranny.common.file.InvalidFileTypeException;
import net.waymire.tyranny.common.util.FileList;

/**
 * This class will populate a ProtocolProcessorRegistry by scanning through a specified directory for any
 * JAR or CLASS files that contain suitable methods with the appropriate annotation.
 *
 * @param <T> The type of socket session that packets will arrive on
 * @Param <P> The type of packets this handler will process
 */
abstract public class ProtocolProcessorRegistryLoader<T,P extends Packet<? extends Opcode>> {	
	private ProtocolProcessorRegistry<T,P> registry;
	private String annotationName;
	
	public ProtocolProcessorRegistryLoader(ProtocolProcessorRegistry<T,P> registry,String annotationName)
	{
		this.registry = registry;
		this.annotationName = annotationName;
	}
	
	public void load(File dir)
	{
		if(dir == null)
		{
			throw new IllegalArgumentException("File argument cannot be NULL.");
		}
		
		if(!dir.exists())
		{
			throw new FileNotFoundException(dir.getAbsolutePath());
		}
		
		if(!dir.isDirectory())
		{
			throw new InvalidFileTypeException(dir.getAbsolutePath());
		}
		
		if(!dir.canRead())
		{
			throw new FileNotReadableException(dir.getAbsolutePath());
		}
		
		AnnotationScanner<File> scanner = new ByteCodeAnnotationScanner();
		List<File> files = FileList.findBySuffix(SystemConstants.JAR_FILE_EXTENSION,dir);

		List<AnnotationScanResult> results = scanner.scan(files, annotationName);
		
		if(results != null)
		{
			for(AnnotationScanResult result : results)
			{
				if(result.getType().equals(ResultType.METHOD))
				{
					processScanResult(result, registry);					
				}
			}
		}
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	protected ProtocolProcessorRegistry<T,P> getRegistry()
	{
		return registry;
	}
	
	protected String getAnnotationName()
	{
		return annotationName;
	}
	
	abstract protected void processScanResult(AnnotationScanResult result,ProtocolProcessorRegistry<T,P> registry);
}
