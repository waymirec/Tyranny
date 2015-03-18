package net.waymire.tyranny.common.protocol;

import java.nio.ByteBuffer;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * This class represents a network packet and is composed of two pieces; a header
 * and a payload. 
 * 
 *  The header is composed of 2 integer values representing the total packet length
 *  and the packet opcode respectively.
 * 
 * @param <O> The specific type of Opcode that this packet will contain.
 */
abstract public class Packet<O extends Opcode> {
	private final PacketBuffer header;
	private final PacketBuffer payload;
	private boolean prepared = false;
	
	
	/**
	 * Instantiate a Packet object with the specific opcode. The packet
	 * size will be initialized to 0 and will be set correctly when the
	 * prepare() method is called.
     *
	 * @param opcode The specified opcode for this packet
	 */
	public Packet(O opcode)
	{
		header = new PacketBuffer(8);
		header.putInt(0);		
		header.putInt(opcode.intValue());
		payload = new PacketBuffer(1024);
	}
	
	protected Packet(PacketBuffer header,PacketBuffer payload)
	{
		this.header = header;
		this.payload = payload;
		this.prepare();
	}
	
	/**
	 * Returns the packet header as a read-only PacketBuffer. Any changes
	 * made to this object will not affect the Packet.
	 * @return
	 */
	public PacketBuffer getHeaderAsReadOnly()
	{
		return header.asReadOnlyBuffer();
	}
	
	/**
	 * Returns the packet payload as a read-only PacketBuffer. Any changes
	 * made to this object will not affect the Packet.
	 * @return
	 */
	public PacketBuffer getPayloadAsReadOnly()
	{
		return payload.asReadOnlyBuffer();
	}
	
	/**
	 * Returns the size of the packet as indicated in the header.
	 * 
	 * This header value is not set until the prepare() method is invoked.
	 * @return
	 */
	public int size()
	{
		return header.getInt(0);
	}
	
	/**
	 * Returns the limit of the payload buffer.
	 * 
	 * @See java.nio.Buffer#limit()
	 * 
	 * @return The limit of this packet's payload buffer
	 */
	public int limit()
	{
		return payload.limit();
	}
	
	/**
	 * Returns the opcode of this packet.
	 * 
	 * @return The opcode of this packet
	 */
	abstract public O opcode();
	
	/**
	 * Finalizes and prepares the packet to be processed/read as well as 
	 * properly sets the packet size in the header.
	 * 
	 * @see java.nio.Buffer#flip()
	 */
	public void prepare()
	{
		if(!prepared)
		{
			payload.prepare();
		
			header.putInt(0,payload.remaining() + 8);
			header.prepare();
			
			prepared = true;
		}
	}
	
	/**
	 * Returns a ByteBuffer composed of the values in the header and payload of this packet.
	 * Changes made to this ByteBuffer object will not affect the packet.
	 * 
	 * @return The packet header and payload in a read-only ByteBuffer
	 */
	public ByteBuffer asReadOnlyByteBuffer()
	{
		byte[] headerBytes = header.array();
		byte[] payloadBytes = payload.array();
		ByteBuffer buff = ByteBuffer.allocate(headerBytes.length + payloadBytes.length);
		buff.put(headerBytes);
		buff.put(payloadBytes);		
		buff.flip();
		return buff;
	}
	
	/**
	 * Returns the number of elements between the current position in this packet's payload and the payload limit.
	 * 
	 * @see java.nio.Buffer#remaining()
	 * 
	 * @return The number of elements remaining in this packet's payload
	 */
	public int remaining()
	{
		return payload.remaining();
	}
	
	/**
	 * Returns this packet's payload buffer position
	 * 
	 * @see java.nio.Buffer#position()
	 * 
	 * @return The position of this packet's payload buffer
	 */	
	public int position()
	{
		return payload.position();
	}
	
	/**
	 * Set this packet's payload buffer mark at its position.
	 * 
	 * @see java.nio.Buffer#mark()
	 * 
	 * @return This packet
	 */
	public Packet<O> mark()
	{
		payload.mark();
		return this;
	}
	
	/**
	 * Reset this packet's payload buffer's position to the previously-marked position.
	 * 
	 * @see java.nio.Buffer#reset()
	 * 
	 * @return This packet
	 */
	public Packet<O> reset()
	{
		payload.reset();
		return this;
	}
	
	/**
	 * Relative get method. Reads the byte at the current position of the payload buffer, 
	 * and then increments the position.
	 * 
	 * @see java.nio.ByteBuffer#get()
	 * 
	 * @return The byte at the payload buffer's current position
	 */
	public byte get()
	{
		return payload.get();
	}
	
	/**
	 * Relative bulk get method.
	 * 
	 * @see java.nio.ByteBuffer#get(byte[])
	 * 
	 * @param dst destination array
	 * 
	 * @return This packet
	 */
	public Packet<O>get(byte[] dst)
	{
		payload.get(dst);
		return this;
	}
	
	public Packet<O> get(byte[] dst, int index)
	{
		payload.get(dst,index);
		return this;
	}
	
	public Packet<O> get(byte[] dst, int offset, int length)
	{
		payload.get(dst,offset,length);
		return this;
	}
	
	public byte get(int index)
	{
		return payload.get(index);
	}
	
	public boolean getBoolean()
	{
		return payload.getBoolean();
	}
	
	public boolean getBoolean(int index)
	{
		return payload.getBoolean(index);
	}
	
	public char getChar()
	{
		return payload.getChar();
	}
	
	public char getChar(int index)
	{
		return payload.getChar(index);
	}
	
	public double getDouble()
	{
		return payload.getDouble();
	}
	
	public double getDouble(int index)
	{
		return payload.getDouble(index);
	}
	
	public float getFloat()
	{
		return payload.getFloat();
	}
	
	public float getFloat(int index)
	{
		return payload.getFloat(index);
	}
	
	public int getInt()
	{
		return payload.getInt();
	}
	
	public int getInt(int index)
	{
		return payload.getInt(index);
	}
	
	public long getLong()
	{
		return payload.getLong();
	}
	
	public long getLong(int index)
	{
		return payload.getLong(index);
	}
	
	public short getShort()
	{
		return payload.getShort();
	}
	
	public short getShort(int index)
	{
		return payload.getShort(index);
	}
	
	public String getString()
	{
		return payload.getString();
	}
	
	public String getString(int index)
	{
		return payload.getString(index);
	}
	
	public Object getObject(Class<?> c)
	{
		return payload.getObject(c);
	}
	
	public Object getObject(int index,Class<?> c)
	{
		return payload.getObject(index,c);
	}
	
	public Packet<O> put(byte b)
	{
		payload.put(b);
		return this;
	}
	
	public Packet<O> put(int index,byte b)
	{
		payload.put(index,b);
		return this;
	}
	
	public Packet<O> put(byte[] src)
	{
		payload.put(src);
		return this;
	}
	
	public Packet<O> put(int index,byte[] src)
	{
		payload.put(index,src);
		return this;
	}
	
	public Packet<O> put(byte[] src,int offset,int length)
	{
		payload.put(src,offset,length);
		return this;
	}
	
	public Packet<O> putBuffer(ByteBuffer src)
	{
		payload.putBuffer(src);
		return this;
	}
	
	public Packet<O> putBuffer(int index,ByteBuffer src)
	{
		payload.putBuffer(index,src);
		return this;
	}
	
	public Packet<O> putBuffer(PacketBuffer src)
	{
		payload.putBuffer(src);
		return this;
	}
	
	public Packet<O> putBuffer(int index,PacketBuffer src)
	{
		payload.putBuffer(index,src);
		return this;
	}
	
	public Packet<O> putBoolean(boolean value)
	{
		payload.putBoolean(value);
		return this;
	}
	
	public Packet<O> putBoolean(int index,boolean value)
	{
		payload.putBoolean(index,value);
		return this;
	}
	
	public Packet<O> putChar(char value)
	{
		payload.putChar(value);
		return this;
	}
	
	public Packet<O> putChar(int index,char value)
	{
		payload.putChar(index,value);
		return this;
	}
	
	public Packet<O> putDouble(double value)
	{
		payload.putDouble(value);
		return this;
	}
	
	public Packet<O> putDouble(int index,double value)
	{
		payload.putDouble(index,value);
		return this;
	}
	
	public Packet<O> putFloat(float value)
	{
		payload.putFloat(value);
		return this;
	}
	
	public Packet<O> putFloat(int index,float value)
	{
		payload.putFloat(index,value);
		return this;
	}
	
	public Packet<O> putInt(int value)
	{
		payload.putInt(value);
		return this;
	}
	
	public Packet<O> putInt(int index,int value)
	{
		payload.putInt(index,value);
		return this;
	}
	
	public Packet<O> putLong(long value)
	{
		payload.putLong(value);
		return this;
	}
	
	public Packet<O> putLong(int index,long value)
	{
		payload.putLong(index,value);
		return this;
	}
	
	public Packet<O> putShort(short value)
	{
		payload.putShort(value);
		return this;
	}
	
	public Packet<O> putShort(int index, short value)
	{
		payload.putShort(index,value);
		return this;
	}
	
	public Packet<O> putString(String str)
	{
		payload.putString(str);
		return this;
	}
	
	public Packet<O> putString(int index, String str)
	{
		payload.putString(index,str);
		return this;
	}
	
	public Packet<O> putObject(Object o)
	{
		payload.putObject(o);
		return this;
	}
	
	public Packet<O> putObject(int index,Object o)
	{
		payload.putObject(index,o);
		return this;
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	protected PacketBuffer getHeader()
	{
		return header;
	}
	
	protected PacketBuffer getPayload()
	{
		return payload;
	}
	
	protected boolean isPrepared()
	{
		return prepared;
	}
}
