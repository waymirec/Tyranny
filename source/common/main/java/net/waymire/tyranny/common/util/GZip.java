package net.waymire.tyranny.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;

final public class GZip {
	private static final Logger LOGGER = Logger.getLogger(GZip.class.getName());
	
	private GZip() { }
	
	public static byte[] compress(byte[] data)
	{
		// Create the compressor with highest level of compression
	    Deflater compressor = new Deflater();
	    compressor.setLevel(Deflater.DEFLATED);
	    
	    // Give the compressor the data to compress
	    compressor.setInput(data);
	    compressor.finish();
	    // Create an expandable byte array to hold the compressed data.
	    // You cannot use an array that's the same size as the orginal because
	    // there is no guarantee that the compressed data will be smaller than
	    // the uncompressed data.
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
	    
	    // Compress the data
	    byte[] buf = new byte[1024];

	    while (!(compressor.finished()))
	    {
	    	int count = compressor.deflate(buf);
	    	bos.write(buf, 0, count);
	    }

	    try {
	        bos.close();
	    } catch (IOException e) {
	    }
	    
	    // Get the compressed data
	    return bos.toByteArray();
	}

	public static byte[] decompress(byte[] data)
	{
		// Create the decompressor and give it the data to compress
	    Inflater decompressor = new Inflater();
	    decompressor.setInput(data);
	    
	    // Create an expandable byte array to hold the decompressed data
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
	    
	    // Decompress the data
	    byte[] buf = new byte[1024];
	    int count;
	    try {
	    	while ((count = decompressor.inflate(buf)) != 0)
	    	{
	        	bos.write(buf, 0, count);
	    	}
	    } catch (DataFormatException e) {
	    }

	    try {
	        bos.close();
	    } catch (IOException e) {
	    	LOGGER.log(Level.SEVERE,ExceptionUtil.getReason(e));
			LOGGER.log(Level.SEVERE,ExceptionUtil.getStackTrace(e));
	    }
	    
	    // Get the decompressed data
	    return bos.toByteArray();
	}	
}
