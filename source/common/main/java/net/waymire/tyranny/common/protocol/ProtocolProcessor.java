package net.waymire.tyranny.common.protocol;

/**
 * A <code>ProtocolProcessor</code> is responsible for processing incoming
 * packets.
 * 
 * @param <T> The type of socket session that packets will arrive on
 * @Param <P> The type of packets this handler will process
 */
public interface ProtocolProcessor<T,P extends Packet<? extends Opcode>> {
	public void process(T source,P packet);
}
