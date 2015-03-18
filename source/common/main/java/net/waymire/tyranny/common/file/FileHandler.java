package net.waymire.tyranny.common.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import net.waymire.tyranny.common.HashContributor;
import net.waymire.tyranny.common.ToString;

public class FileHandler 
{	
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final int BUFFER_SIZE=1024;
	public static final int OPENED = 1;
	public static final int CLOSED = 2;
	public static final int FILE_START = 0;
	public static final int FILE_END = -1;
	private static final int DEFAULT_STATE = CLOSED;
	
	@HashContributor
	@ToString
	// File we are wrapping
	private final File m_file;
	
	// Random Access view of the file
	private RandomAccessFile m_raf;
	
	@HashContributor
	@ToString
	// Default state is CLOSED
	private int m_state = DEFAULT_STATE;
	
	@HashContributor
	@ToString
	// Last time we marked the file as being modified
	private long m_lastKnownModified;
	
	public FileHandler(File file)
	{
		if(file == null)
			throw new IllegalArgumentException();
		
		m_file = file;
		setLastKnownModified();
	}
	
	public FileHandler(String filename)
	{
		this(new File(filename));
	}

	
	public synchronized void open() throws FileNotFoundException,FileReadException
	{
		if(isOpened())
			return;
		
		setState(OPENED);
		setRandomAccessFile();
		seek(FILE_START);
	}
	
	public synchronized void close() throws FileCloseException
	{
		if(isClosed())
			return;
		
		try
		{
			getRandomAccessFile().close();
		}
		catch (IOException ioe)
		{
			throw new FileCloseException(ioe);
		}
		finally
		{
			setRandomAccessFile(null);
			setState(CLOSED);
		}
	}
	
	public boolean isOpened()
	{
		return getState() == OPENED;
	}
	
	public boolean isClosed()
	{
		return getState() == CLOSED;
	}
	
	public boolean canRead()
	{
		return getFile().canRead();
	}
	
	public boolean canWrite()
	{
		return getFile().canWrite();
	}

	public long getLastModified()
	{
		return getFile().lastModified();
	}
	
	public boolean isDirty()
	{
		return getLastKnownModified() != getLastModified();
	}
	
	public synchronized void seek(int position) throws FileReadException
	{
		assertState(OPENED);		
		try
		{
			if(position == FILE_START)
			{
				getRandomAccessFile().seek(0);
				return;
			}
		
			if(position == FILE_END)
			{
				getRandomAccessFile().seek(getRandomAccessFile().getChannel().size());
				return;
			}
		
			getRandomAccessFile().seek(position);
		}
		catch (IOException ioe)
		{
			throw new FileReadException(ioe);
		}
	}
	
	public synchronized void clear() throws FileWriteException
	{
		truncate(0);
	}

	public synchronized void rewind() throws FileReadException
	{
		seek(FILE_START);
	}
	
	public synchronized void forward() throws FileReadException
	{
		seek(FILE_END);
	}
	
	public synchronized void truncate(long size) throws FileWriteException
	{
		assertState(OPENED);
		try
		{
			getRandomAccessFile().getChannel().truncate(size);
			getRandomAccessFile().seek(size);
			setLastKnownModified();
		}
		catch (IOException ioe)
		{
			throw new FileWriteException(ioe);
		}
	}
	
	public synchronized String read() throws FileReadException
	{
		assertState(OPENED);
		StringBuilder sb = new StringBuilder();
		List<String> lines = readArray();
		for(String line : lines)
		{
			sb.append(line);
		}
		return sb.toString();
	}
	
	public synchronized List<String> readArray() throws FileReadException
	{
		assertState(OPENED);
		rewind();
		List<String> lines = new ArrayList<String>();
		String line;		
		try
		{
			while((line = getRandomAccessFile().readLine()) != null)
			{
				lines.add(line);
			}
			return lines;
		}
		catch (IOException ioe)
		{
			throw new FileReadException(ioe);
		}		
	}
	
	public String readLine() throws FileReadException
	{
		assertState(OPENED);
		try
		{
			return getRandomAccessFile().readLine();
		}
		catch (IOException ioe)
		{
			throw new FileReadException(ioe);
		}
	}	
	
	public synchronized void write(String line) throws FileWriteException
	{
		assertState(OPENED);
		try
		{
			getRandomAccessFile().write(line.getBytes());
			setLastKnownModified();
		}
		catch (IOException ioe)
		{
			throw new FileWriteException(ioe);
		}
	}
	
	public synchronized void write(List<String> contents) throws FileWriteException
	{
		assertState(OPENED);
		clear();
		for(String line : contents)
		{
			if(line == null)
				continue;			
			writeLine(line);
		}
	}
	
	public synchronized void writeLine(String line) throws FileWriteException
	{
		assertState(OPENED);
		try
		{
			getRandomAccessFile().write(line.getBytes());
			getRandomAccessFile().write(NEWLINE.getBytes());
			setLastKnownModified();
		}
		catch (IOException ioe)
		{
			throw new FileWriteException(ioe);
		}
	}
	
	private void assertState(int state)
	{
		if(getState() != state)
		{
			throw new IllegalStateException();
		}
	}
	
	private synchronized void setState(int state)
	{
		m_state = state;
	}
	
	private synchronized void setLastKnownModified()
	{
		m_lastKnownModified = getLastModified();
	}

	private synchronized int getState()
	{
		return m_state;
	}

	private synchronized long getLastKnownModified()
	{
		return m_lastKnownModified;
	}
	
	private synchronized File getFile()
	{
		return m_file;
	}
	
	private RandomAccessFile getRandomAccessFile()
	{
		return m_raf;
	}
	
	private void setRandomAccessFile(RandomAccessFile raf)
	{
		m_raf = raf;
	}
	
	private void setRandomAccessFile() throws FileNotFoundException
	{
		setRandomAccessFile(new RandomAccessFile(m_file,"rw"));
	}
}