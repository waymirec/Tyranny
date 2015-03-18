package net.waymire.tyranny.common.net;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

/**
 * This interface represents the handler class that is used to
 * process network events from a UDP server.
 * 
 *
 * @param <T> The type of Packet this handler will process
 */
public interface UdpServerHandler<T extends Packet<? extends Opcode>> extends IpServerHandler<UdpSession, T>
{

}
