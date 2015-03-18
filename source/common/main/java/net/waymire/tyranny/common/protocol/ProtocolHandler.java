package net.waymire.tyranny.common.protocol;

/**
 * A <code>ProtocolHandler</code> is responsible for accepting incoming
 * messages and forwarding them on to designated <code>ProtocolProcessor</code>s
 * 
 * @param <T> The type of socket session that packets will arrive on
 * @Param <P> The type of packets this handler will process
 */
public interface ProtocolHandler<T,P extends Packet<? extends Opcode>> 
{
	public void handle(T source,P packet);
}
