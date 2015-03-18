package net.waymire.tyranny.common.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.util.ByteUtil;

/** 
 * A byte buffer used for packet handling. This class is a wrapper around the ByteBuffer class.
 * 
 */
public class PacketBuffer implements Cloneable {
	public static final int DEFAULT_PACKET_SIZE = 1024;
	
	private final int initialCapacity;
	private ByteBuffer buffer;
	private int index;
	private boolean isReady;
	private int mark;
	private int endOfPacket = 0;

	public static PacketBuffer wrap(ByteBuffer source)
	{
		PacketBuffer pb = new PacketBuffer(source.limit());
		pb.buffer = source;
		pb.prepare();
		return pb;
	}
	
	public static PacketBuffer wrap(ByteBuffer source,int index,int len)
	{
		PacketBuffer pb = new PacketBuffer(len);
		byte[] bytes = new byte[len];
		source.mark();
		source.position(index);
		source.get(bytes);
		source.reset();
		pb.put(bytes);
		pb.prepare();
		return pb;
	}
	
	public static PacketBuffer wrap(byte[] source)
	{
		return PacketBuffer.wrap(source,0,source.length);
	}
	
	public static PacketBuffer wrap(byte[] source,int index,int len)
	{
		PacketBuffer pb = new PacketBuffer(len);
		pb.put(source,index,len);
		pb.prepare();
		return pb;
	}
	
	public static PacketBuffer wrap(PacketBuffer source)
	{
		PacketBuffer pb = new PacketBuffer(source.limit());
		pb.buffer = source.asReadOnlyByteBuffer();
		pb.prepare();
		return pb;
	}
	 
	public PacketBuffer(int size)
	{
		initialCapacity = size;
		buffer = ByteBuffer.allocate(size);
	}
	
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public int getEndOfPacket() {
		return endOfPacket;
	}

	public void setEndOfPacket(int END_OF_PACKET) {
		this.endOfPacket = END_OF_PACKET;
	}

	public int getInitialCapacity() {
		return initialCapacity;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	protected void prepare()
	{
		if(buffer.position() != 0)
		{
			//int size = endOfPacket > 4 ? endOfPacket-4 : 0;
			//buffer.putInt(0,size);
			buffer.position(endOfPacket);
			buffer.flip();
		}
	}
	
	public ByteBuffer encode()
	{
		prepare();
		return (ByteBuffer)this.asReadOnlyByteBuffer().rewind();
	}
	
	public void decode(byte[] source,int index,int len)
	{
		buffer = ByteBuffer.allocate(len);
		buffer.put(source,index,len);
		prepare();
	}
	
	public void decode(byte[] source)
	{
		decode(source,0,source.length);
	}
	
	/* ByteBuffer functionality */
	public PacketBuffer asReadOnlyBuffer()
	{
		buffer.mark();
		ByteBuffer ro = buffer.asReadOnlyBuffer();
		PacketBuffer buff = PacketBuffer.wrap(ro);
		buffer.reset();
		return buff;
	}
	
	public ByteBuffer asReadOnlyByteBuffer()
	{
		buffer.mark();
		ByteBuffer ro = buffer.asReadOnlyBuffer();
		buffer.reset();
		return ro;
	}
	
	public byte[] array()
	{
		PacketBuffer clone = this.clone();
		clone.getBuffer().compact();
		return clone.getBuffer().array();
	}
	
	public PacketBuffer compact()
	{
		buffer.compact();
		return this;
	}
	
	public byte get()
	{
		return buffer.get();
	}
	
	public PacketBuffer get(byte[] dst)
	{
		buffer.get(dst);
		return this;
	}
	
	public PacketBuffer get(byte[] dst, int index)
	{
		buffer.mark();
		try
		{
			this.position(index);
			this.get(dst);
			return this;
		}
		finally
		{
			buffer.reset();
		}
	}
	
	public PacketBuffer get(byte[] dst, int offset, int length)
	{
		buffer.get(dst,offset,length);
		return this;
	}
	
	public byte get(int index)
	{
		return buffer.get(index);
	}
	
	public boolean getBoolean()
	{
		return (buffer.get() == 1);
	}
	
	public boolean getBoolean(int index)
	{
		return (buffer.get(index) == 1);
	}
	
	public char getChar()
	{
		return buffer.getChar();
	}
	
	public char getChar(int index)
	{
		return buffer.getChar(index);		
	}
	
	public double getDouble()
	{
		return buffer.getDouble();
	}
	
	public double getDouble(int index)
	{
		return buffer.getDouble(index);
	}
	
	public float getFloat()
	{
		return buffer.getFloat();
	}
	
	public float getFloat(int index)
	{
		return buffer.getFloat(index);
	}
	
	public int getInt()
	{
		return buffer.getInt();
	}
	
	public int getInt(int index)
	{
		return buffer.getInt(index);
	}
	
	public long getLong()
	{
		return buffer.getLong();
	}
	
	public long getLong(int index)
	{
		return buffer.getLong(index);
	}
	
	public short getShort()
	{
		return buffer.getShort();
	}
	
	public short getShort(int index)
	{
		return buffer.getShort(index);
	}

	public String getString(int index)
	{
		this.mark();
		this.position(index);
		String ret = this.getString();
		this.reset();
		return ret;
	}
	
	public String getString()
	{
		short length = buffer.getShort();
		byte[] bytes = new byte[length];
		this.get(bytes);
		String ret = new String(bytes);
		return ret;
	}

	public Object getObject(Class<?> c)
	{
		return getObject(-1,c);
	}
	
	public Object getObject(int index,Class<?> c)
	{
		final boolean hasIndex = (index > 0);
		Object retval = null;

		if ((c == byte.class) || (c == Byte.class)) {
			retval = hasIndex ? get(index) : get();
		}
			
		if ((c == boolean.class) || (c == Boolean.class)) {
			retval =  hasIndex ? getBoolean(index) : getBoolean();
		}

		if ((c == char.class) || (c == Character.class)) {
			retval =  hasIndex ? getChar(index) : getChar();
		}

		if ((c == short.class) || (c == Short.class)) {
			retval =  hasIndex ? getShort(index) : getShort();
		}

		if ((c == int.class) || (c == Integer.class)) {
			retval =  hasIndex ? getInt(index) : getInt();
		}

		if ((c == double.class) || (c == Double.class)) {
			retval =  hasIndex ? getDouble(index) : getDouble();
		}

		if ((c == float.class) || (c == Float.class)) {
			retval =  hasIndex ? getFloat(index) : getFloat();
		}

		if ((c == long.class) || (c == Long.class)) {
			retval =  hasIndex ? getLong(index) : getLong();
		}

		if (c == String.class) {
			retval =  hasIndex ? getString(index) : getString();
		}

		if(retval == null)
		{
			throw new IllegalArgumentException(c.getName());
		}
		
		return retval;
	}
	
	public PacketBuffer put(byte b)
	{
		checkAndAdjustSize(1);
		buffer.put(b);
		update(1);
		return this;
	}
	
	public PacketBuffer put(int index,byte b)
	{
		checkAndAdjustSize(index,1);
		buffer.put(index,b);
		updateDelta(index+1);
		return this;
	}
	
	public PacketBuffer put(byte[] src)
	{
		checkAndAdjustSize(src.length);
		buffer.put(src);
		update(src.length);
		return this;
	}
	
	public PacketBuffer put(int index,byte[] src)
	{
		checkAndAdjustSize(index,src.length);
		buffer.put(src);
		updateDelta(index+src.length);
		return this;
	}
	
	public PacketBuffer put(byte[] src,int offset,int length)
	{
		checkAndAdjustSize(length);
		buffer.put(src,offset,length);
		update(length);
		return this;
	}
	
	public PacketBuffer putBuffer(ByteBuffer src)
	{
		int len = src.remaining();
		checkAndAdjustSize(len);
		buffer.put(src);
		update(len);
		return this;
	}
	
	public PacketBuffer putBuffer(int index,ByteBuffer src)
	{
		int len = src.remaining();
		checkAndAdjustSize(index,len);
		buffer.put(src);
		updateDelta(index+len);
		return this;
	}
	
	public PacketBuffer putBuffer(PacketBuffer src)
	{
		byte[] bytes = src.array();
		put(bytes);
		return this;
	}
	
	public PacketBuffer putBuffer(int index,PacketBuffer src)
	{
		byte[] bytes = src.array();
		put(index,bytes);
		return this;
	}
	
	public PacketBuffer putBoolean(boolean value)
	{
		checkAndAdjustSize(1);
		byte val = (byte)(value ? 1 : 0);
		buffer.put(val);
		update(1);
		return this;
	}
	
	public PacketBuffer putBoolean(int index,boolean value)
	{
		checkAndAdjustSize(index,1);
		byte val = (byte)(value ? 1 : 0);
		buffer.put(index,val);
		updateDelta(index+1);
		return this;
	}
	
	public PacketBuffer putChar(char value)
	{
		checkAndAdjustSize(1);
		buffer.putChar(value);
		update(1);
		return this;
	}
	
	public PacketBuffer putChar(int index,char value)
	{
		checkAndAdjustSize(index,1);
		buffer.putChar(index,value);
		update(1);
		return this;
	}
	
	public PacketBuffer putDouble(double value)
	{
		checkAndAdjustSize(8);
		buffer.putDouble(value);
		update(8);
		return this;
	}
	
	public PacketBuffer putDouble(int index,double value)
	{
		checkAndAdjustSize(index,8);
		buffer.putDouble(index,value);		
		updateDelta(index+8);
		return this;
	}
	
	public PacketBuffer putFloat(float value)
	{
		checkAndAdjustSize(4);
		buffer.putFloat(value);
		update(4);
		return this;
	}
	
	public PacketBuffer putFloat(int index,float value)
	{
		checkAndAdjustSize(index,4);
		buffer.putFloat(index,value);
		updateDelta(index+4);
		return this;
	}
	
	public PacketBuffer putInt(int value)
	{
		checkAndAdjustSize(4);
		buffer.putInt(value);
		update(4);
		return this;
	}
	
	public PacketBuffer putInt(int index,int value)
	{
		checkAndAdjustSize(index,4);
		buffer.putInt(index,value);
		updateDelta(index+4);
		return this;
	}
	
	public PacketBuffer putLong(long value)
	{
		checkAndAdjustSize(8);
		buffer.putLong(value);
		update(8);
		return this;
	}
	
	public PacketBuffer putLong(int index,long value)
	{
		checkAndAdjustSize(index,8);
		buffer.putLong(index,value);
		updateDelta(index+8);
		return this;
	}
	
	public PacketBuffer putShort(short value)
	{
		checkAndAdjustSize(2);
		buffer.putShort(value);
		update(2);
		return this;
	}
	
	public PacketBuffer putShort(int index, short value)
	{
		checkAndAdjustSize(index,2);
		buffer.putShort(index,value);
		updateDelta(index+2);
		return this;
	}

	public PacketBuffer putString(String str)
	{
		if(str == null)
		{
			str = "";
		}

		checkAndAdjustSize(str.length());
		byte[] bytes = str.getBytes();		
		buffer.putShort((short)bytes.length);
		buffer.put(bytes);
		update(2 + bytes.length);
		return this;
	}
	
	public PacketBuffer putString(int index, String str)
	{
		checkAndAdjustSize(index,str.length());
		buffer.mark();
		buffer.position(index);
		byte[] bytes = str.getBytes();
		buffer.putShort((short)bytes.length);
		buffer.put(bytes);
		buffer.reset();
		updateDelta(index+2+bytes.length);
		return this;
	}
	
	public PacketBuffer putObject(Object o)
	{
		putObject(-1,o);
		return this;
	}
	
	public PacketBuffer putObject(int index,Object o)
	{
		final boolean hasIndex = (index > 0);
		Class<?> c = o.getClass();
			
		if((c == byte.class) || (c == Byte.class)) {
			return (hasIndex) ? put(index, (Byte) o) : put((Byte) o); 
		}

		if(o instanceof byte[]) {
			putObject(index,ByteUtil.box((byte[])o));
			return this;
		}
			
		if (o instanceof Byte[]) {
			return (hasIndex) ? put(index, ByteUtil.unbox((Byte[]) o)) : put(ByteUtil.unbox((Byte[]) o)); 
		}

		if((c == boolean.class) || (c == Boolean.class)) {
			return (hasIndex) ? putBoolean(index, (Boolean) o) : putBoolean((Boolean) o);
		}

		if((c == char.class) || (c == Character.class)) {
			return (hasIndex) ? putChar(index, (Character) o) : putChar((Character) o); 
		}

		if((c == short.class) || (c == Short.class)) {
			return (hasIndex) ? putShort(index, (Short) o) : putShort((Short) o);
		}

		if((c == int.class) || (c == Integer.class)) {
			return (hasIndex) ? putInt(index, (Integer) o) : putInt((Integer) o); 
		}

		if((c == double.class) || (c == Double.class)) {
			return (hasIndex) ? putDouble(index, (Double) o) : putDouble((Double) o);
		}

		if((c == float.class) || (c == Float.class)) {
			return (hasIndex) ? putFloat(index, (Float) o) : putFloat((Float) o);
		}

		if((c == long.class) || (c == Long.class)) {
			return (hasIndex) ? putLong(index, (Long) o) : putLong((Long) o);
		}

		if (o instanceof String) {
			return (hasIndex) ? putString(index, (String) o) : putString((String) o); 
		}

		if (o instanceof ByteBuffer) {
			return (hasIndex) ? putBuffer(index, (ByteBuffer) o) : putBuffer((ByteBuffer) o);
		}

		if (o instanceof PacketBuffer) {
			return (hasIndex) ? putBuffer(index, ((PacketBuffer) o).asReadOnlyByteBuffer()) : putBuffer(((PacketBuffer) o).asReadOnlyByteBuffer());
		}

		throw new IllegalArgumentException(o.toString());
	}
	
	public int capacity()
	{
		return buffer.capacity();
	}
	
	public PacketBuffer clear()
	{
		buffer.clear();
		return this;
	}

	public PacketBuffer flip()
	{
		if (buffer.position() > 0)
		{
			buffer.flip();
		}
		return this;
	}
	
	public boolean hasRemaining()
	{
		return buffer.hasRemaining();
	}
	
	public boolean hasRoom(int len)
	{
		int size = (this.position() + len);
		return (size <= this.capacity());
	}
	
	public boolean canPut(int index,int len)
	{
		return ((index+len) <= this.capacity());		
	}
	
	public int size()
	{
		return endOfPacket;
	}
	
	public int limit()
	{
		return buffer.limit();
	}
	
	public PacketBuffer limit(int size)
	{
		buffer.limit(size);
		return this;
	}
	
	public PacketBuffer mark()
	{
		buffer.mark();
		return this;
	}
	
	public int position()
	{		
		return buffer.position();
	}
	
	public PacketBuffer position(int position)
	{
		if((position < 0) || (position > endOfPacket))
		{
			throw new IndexOutOfBoundsException();					
		}
		
		buffer.position(position);
		return this;
	}
	
	public int remaining()
	{
		return buffer.remaining();
	}
	
	public PacketBuffer reset()
	{
		buffer.reset();
		return this;
	}
	
	public PacketBuffer rewind()
	{
		buffer.rewind();
		return this;
	}
	
	public PacketBuffer slice()
	{
		buffer.slice();
		return this;
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	@Override
	public int hashCode()
	{
		return buffer.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return ((o instanceof PacketBuffer) && (((PacketBuffer)o).hashCode() == this.hashCode()));
	}
	
	public boolean isDirect()
	{
		return buffer.isDirect();
	}
	
	public ByteOrder order()
	{
		return buffer.order();
	}	

	public PacketBuffer clone()
	{
		int size,position,limit;
		byte[] data;
		synchronized(this) {
			size = buffer.limit();
			position = buffer.position();
			limit = buffer.limit();
			buffer.mark();
			buffer.position(0);
			data = new byte[size];
			buffer.get(data);
			buffer.reset();
		}
		
		PacketBuffer clone = new PacketBuffer(size);
		clone.put(data);
		clone.position(position);
		clone.limit(limit);		
		return clone;
	}
	
	protected ByteBuffer getBuffer()
	{
		return buffer;
	}	
	
	private void checkAndAdjustSize(int len)
	{
		checkAndAdjustSize(buffer.position(),len);
	}
	
	private void checkAndAdjustSize(int pos,int len)
	{
		if (pos > endOfPacket) {
			throw new IndexOutOfBoundsException();
		}

		int size = (pos + len) - buffer.position();
		if (!hasRoom(size)) {
			int position = buffer.position();
			buffer.flip();
			ByteBuffer buff = ByteBuffer.allocate((int) (buffer.capacity() + initialCapacity));
			buff.put(buffer);
			buff.position(position);
			buffer = buff;
			checkAndAdjustSize(pos, len);
		}
	}
	
	private void update(int len)
	{
		endOfPacket += len;		
		if(buffer.limit() < endOfPacket)
		{
			buffer.limit(endOfPacket);
		}
	}
	
	private void updateDelta(int val)
	{
		int delta = val - endOfPacket;
		if (delta > 0)
		{
			update(delta);
		}
	}
}