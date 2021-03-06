package net.waymire.tyranny.common.protocol;

import net.waymire.tyranny.common.annotation.AnnotationScanResult;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.util.ExceptionUtil;

public class AuthControlPacketProcessorRegistryLoader extends ProtocolProcessorRegistryLoader<TcpSession,AuthControlPacket> 
{
	
	public AuthControlPacketProcessorRegistryLoader(ProtocolProcessorRegistry<TcpSession,AuthControlPacket> registry,String annotationName)
	{
		super(registry,annotationName);
	}
	
	@Override
	protected void processScanResult(AnnotationScanResult result,ProtocolProcessorRegistry<TcpSession,AuthControlPacket> registry)
	{
		try
		{
			Class<?> clazz = Class.forName(result.getClassName());
			AuthControlOpcode opcode = AuthControlOpcode.valueOf((String)result.getMember("opcode"));
			ProtocolProcessor<TcpSession,AuthControlPacket> delegate = new ProtocolProcessorDelegate<TcpSession,AuthControlPacket>(clazz,result.getTargetName(), new Class<?>[] { TcpSession.class,AuthControlPacket.class });
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this,  "Registering packet handler [{0}.{1}] for opcode [{2}].", result.getClassName(),result.getTargetName(), opcode);
			}
			registry.register(opcode, delegate);
		}
		catch(Exception e)
		{
			LogHelper.severe(this, "Failed to load protocol handler {0}.{1}",result.getClassName(), result.getTargetName());
			LogHelper.severe(this, ExceptionUtil.getReason(e));
			LogHelper.severe(this, ExceptionUtil.getStackTrace(e));
		}
	}
}