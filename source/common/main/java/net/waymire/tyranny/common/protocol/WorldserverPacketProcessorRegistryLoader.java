package net.waymire.tyranny.common.protocol;

import net.waymire.tyranny.common.annotation.AnnotationScanResult;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.util.ExceptionUtil;

public class WorldserverPacketProcessorRegistryLoader extends ProtocolProcessorRegistryLoader<TcpSession, WorldserverPacket> 
{
	public WorldserverPacketProcessorRegistryLoader(ProtocolProcessorRegistry<TcpSession,WorldserverPacket> registry,String annotationName)
	{
		super(registry,annotationName);
	}
	
	@Override
	protected void processScanResult(AnnotationScanResult result,ProtocolProcessorRegistry<TcpSession,WorldserverPacket> registry)
	{
		try
		{
			Class<?> clazz = Class.forName(result.getClassName());
			WorldserverOpcode opcode = WorldserverOpcode.valueOf((String)result.getMember("opcode"));

			ProtocolProcessor<TcpSession,WorldserverPacket> delegate = new ProtocolProcessorDelegate<TcpSession,WorldserverPacket>(clazz,result.getTargetName());
			registry.register(opcode, delegate);
		}
		catch(Exception e)
		{
			LogHelper.severe(this, "Failed to load protocol handler {0}.{1}",new Object[]{result.getClassName(),result.getTargetName()});
			LogHelper.severe(this, ExceptionUtil.getReason(e));
			LogHelper.severe(this, ExceptionUtil.getStackTrace(e));
		}
	}
}
