package net.waymire.tyranny.common.protocol;

/**
 * This interface represents a class that provides a publish/subscribe manner of registration
 * for ProtocolProcessor's interested in receiving packets with a specific opcode.
 * 
 * @param <T> The type of socket session that packets will arrive on
 * @Param <P> The type of packets this handler will process
 */
public interface ProtocolProcessorRegistry<T,P extends Packet<? extends Opcode>> {
	public void start();
	public void stop();
	public void register(Opcode opcode,ProtocolProcessor<T,P> processor);
	public void deregister(Opcode opcode,ProtocolProcessor<T,P> processor);
	public void deregister(Opcode opcode);
}
