package net.waymire.tyranny.common.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Random;

final public class ByteUtil {
	private ByteUtil() { }
	
	public static Byte[] box(byte[] bytes)
	{
		Byte[] boxed = new Byte[bytes.length];
		for(int i=0; i<bytes.length; i++)
		{
			boxed[i] = Byte.valueOf(bytes[i]);
		}
		return boxed;
	}
	
	public static byte[] unbox(Byte[] bytes)
	{
		byte[] unboxed = new byte[bytes.length];
		for(int i=0; i<bytes.length; i++)
		{
			unboxed[i] = bytes[i].byteValue();
		}
		return unboxed;
	}
	
	public static byte[] nullTerminate(byte[] bytes)
	{
		byte[] bytes2 = new byte[bytes.length+1];
		System.arraycopy(bytes,0,bytes2,0,bytes.length);
		bytes2[bytes.length] = (byte)0;
		return bytes2;
	}

	public static final byte[] shortToByteArray(short value)
	{
		return new byte[] {
				(byte)((value >> 8) & 0xFF),
				(byte)((value >> 0) & 0xFF),
		};
	}
	
	public static final short byteArrayToShort(byte[] b)
	{
		return (short)((0xFF & b[0]) << 8 | (0xFF & b[1]) << 0);
	}
	
	public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
	}
	
    public static int byteArrayToInt(byte[] b) {
        return byteArrayToInt(b, 0);
    }
    
    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }
    		
	public static byte[] longToByteArray(long l) {  
		byte b[] = new byte[8];  

		ByteBuffer buf = ByteBuffer.wrap(b);  
		buf.putLong(l);  
		return b;  
	}  
	
	public static long byteArrayToLong(byte[] b) {  
		ByteBuffer buf = ByteBuffer.wrap(b);  
		return buf.getLong();  
	}  
		  
	public static byte[] floatToByteArray(float value)
	{
		byte[] b = new byte[4];
		int intBits=Float.floatToIntBits(value);
		b[0]=(byte)((intBits&0x000000ff)>>0);
		b[1]=(byte)((intBits&0x0000ff00)>>8);
		b[2]=(byte)((intBits&0x00ff0000)>>16);
		b[3]=(byte)((intBits&0xff000000)>>24);
		return b;
	}
		 
	public static float byteArrayToFloat(byte[] bytes)
	{
		int tempbits=((0xff & bytes[3]) | ((0xff & bytes[2]) << 8 ) | ((0xff & bytes[1]) << 16)|((0xff & bytes[0]) <<24));		    
		return Float.intBitsToFloat(tempbits);		 
	}

	public static byte[] flipBytes(byte[] bytes)
	{
		byte[] tmp = new byte[bytes.length];
		for(int i=0; i < bytes.length; i++)
		{
			tmp[i] = bytes[bytes.length - 1 -i];
		}
		return tmp;
	}		
			
	public static BigInteger bigIntegerReverse(BigInteger big)
	{
		byte[] bytes = bigIntegerBytes(big);	
		return new BigInteger(1,flipBytes(bytes));
	}
		
	public static String bytesToHex(byte[] data)
	{		
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++)
		{
			buf.append(String.format("%02x",data[i]));
		}
		return buf.toString();
	}
	
	public static byte[] hexToBytes( String hexStr )
	{
	    byte bArray[] = new byte[hexStr.length()/2];  
	    for(int i=0; i<(hexStr.length()/2); i++){
	    	byte firstNibble  = Byte.parseByte(hexStr.substring(2*i,2*i+1),16); // [x,y)
	    	byte secondNibble = Byte.parseByte(hexStr.substring(2*i+1,2*i+2),16);
	    	int finalByte = (secondNibble) | (firstNibble << 4 ); // bit-operations only with numbers, not bytes.
	    	bArray[i] = (byte) finalByte;
	    }
	    return bArray;
	}
	
	public static String intToHex(int val)
	{
		byte[] bytes = ByteUtil.intToByteArray(val);
		return ByteUtil.bytesToHex(bytes);
	}
	
	public static byte[] bigIntegerBytes(BigInteger big)
	{
		byte[] bytes = big.toByteArray();
		
		if (bytes.length > 1)
		{
			if (bytes.length % 2 != 0)
			{
				byte[] ret = new byte[bytes.length - 1];
				System.arraycopy(bytes,1,ret,0,bytes.length-1);
				return ret;
			}				
		}
		return bytes;
	}
	
	public static byte getByte(byte[] bytes, int index)
	{
		return bytes[index];
	}

	public static short getShort(byte[] bytes, int index)
	{
		byte[] tmp = new byte[2];
		System.arraycopy(bytes,index,tmp,0,2);
		return ByteUtil.byteArrayToShort(tmp);
	}
	
	public static int getInt(byte[] bytes, int index)
	{
		byte[] tmp = new byte[4]; 
		System.arraycopy(bytes,index,tmp,0,4);
		return ByteUtil.byteArrayToInt(tmp);		
	}
	
	public static long getLong(byte[] bytes, int index)
	{
		byte[] tmp = new byte[8];
		System.arraycopy(bytes,index,tmp,0,8);
		return ByteUtil.byteArrayToLong(tmp);
	}
	
	public static float getFloat(byte[] bytes, int index)
	{
		byte[] tmp = new byte[4];
		System.arraycopy(bytes,index,tmp,0,4);
		return ByteUtil.byteArrayToFloat(tmp);
	}
	
	public static String getString(byte[] bytes, int index)
	{
		StringBuffer sb = new StringBuffer();
		while (bytes[index] != 0)
		{
			sb.append((char)bytes[index]);
			index++;
		}
		return sb.toString();
	}
	
	public static short unsignedByte(byte val)
	{
		return (short)(val & 0xFF);
	}
	
	public static int unsignedShort(short val)
	{
		return (int)(val & 0xFFFF);
	}
	
	public static long unsignedInt(int val)
	{
		return (long)(val & 0xFFFFFFFF);
	}
	
	public static BigInteger unsignedLong(long val)
	{
		return new BigInteger(Long.toHexString(val),16);
	}
	
	public static byte signedByte(short val)
	{
		return (byte)val;
	}

	public static short signedShort(int val)
	{
		return (short)val;
	}

	public static int signedInt(long val)
	{
		return (int)val;
	}
	
	public static long signedLong(BigInteger bi)
	{
		return bi.longValue();
	}
	
	public static byte reverseBits(byte in) {
	    byte out = 0;
	    for (int ii = 0 ; ii < 8 ; ii++) {
	        byte bit = (byte)(in & 1);
	        out = (byte)((out << 1) | bit);
	        in = (byte)(in >> 1);
	    }
	    return out;
	}
	
	public static byte[] random(int length)
	{
		byte[] bytes = new byte[length];
		Random random = new Random();
		random.nextBytes(bytes);
		return bytes;
	}
}
