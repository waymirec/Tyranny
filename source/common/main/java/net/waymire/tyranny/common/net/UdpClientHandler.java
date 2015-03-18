package net.waymire.tyranny.common.net;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public interface UdpClientHandler<T extends Packet<? extends Opcode>> extends IpClientHandler<UdpSession, T> 
{

}
