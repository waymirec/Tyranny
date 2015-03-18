package net.waymire.tyranny.common.protocol;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.delegate.DelegateImpl;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.ProtocolHandler;

/**
 * This class is used to create a delegate of a class method that implements the ProtocolHandler interface.
 *  
 * @param <T> The type of socket session that packets will arrive on
 * @param <P> The type of packets this handler will process
 */
public class ProtocolHandlerDelegate<T,P extends Packet<? extends Opcode>> extends DelegateImpl implements ProtocolHandler<T,P> {
	private static final Class<?>[] argTypes = { TcpSession.class,Packet.class };
	
	public ProtocolHandlerDelegate(Object source,String methodName) {
		super(source,methodName,argTypes);
	}
	
	@Override
	public void handle(T source,P packet) {
		invoke(source,packet);
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
