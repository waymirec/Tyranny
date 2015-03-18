package net.waymire.tyranny.common.protocol;

import net.waymire.tyranny.common.annotation.AnnotationScanResult;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.util.ExceptionUtil;

public class LoginserverPacketProcessorRegistryLoader extends ProtocolProcessorRegistryLoader<TcpSession, LoginserverPacket> 
{
	public LoginserverPacketProcessorRegistryLoader(ProtocolProcessorRegistry<TcpSession,LoginserverPacket> registry,String annotationName)
	{
		super(registry,annotationName);
	}
	
	@Override
	protected void processScanResult(AnnotationScanResult result,ProtocolProcessorRegistry<TcpSession,LoginserverPacket> registry)
	{
		try
		{
			Class<?> clazz = Class.forName(result.getClassName());
			LoginserverOpcode opcode = LoginserverOpcode.valueOf((String)result.getMember("opcode"));

			ProtocolProcessor<TcpSession,LoginserverPacket> delegate = new ProtocolProcessorDelegate<TcpSession,LoginserverPacket>(clazz,result.getTargetName(),new Class<?>[]{ TcpSession.class,LoginserverPacket.class });
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this,  "Registering packet handler [{0}.{1}] for opcode [{2}].", result.getClassName(),result.getTargetName(), opcode);
			}
			registry.register(opcode, delegate);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogHelper.severe(this, "Failed to load protocol handler {0}.{1}",new Object[]{result.getClassName(),result.getTargetName()});
			LogHelper.severe(this, ExceptionUtil.getReason(e));
			LogHelper.severe(this, ExceptionUtil.getStackTrace(e));
		}
	}
}
