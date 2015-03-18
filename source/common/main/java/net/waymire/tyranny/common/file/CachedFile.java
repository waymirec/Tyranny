package net.waymire.tyranny.common.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CachedFile {
	private FileHandler m_fileHandler;
	private final List<String> m_contents = new ArrayList<String>();
	
	public CachedFile(File file)
	{
		setFileHandler(new FileHandler(file));
	}

	public CachedFile(String filename)
	{
		this(new File(filename));
	}
	
	public void open() throws FileNotFoundException,FileReadException
	{
		refresh();
	}
	
	public void close() throws FileCloseException
	{
		getFileHandler().close();
	}
	
	public boolean isOpened()
	{
		return getFileHandler().isOpened();
	}
	
	public boolean isClosed()
	{
		return getFileHandler().isClosed();
	}
	
	public boolean canRead()
	{
		return getFileHandler().canRead();
	}
	
	public boolean canWrite()
	{
		return getFileHandler().canWrite();
	}

	public long getLastModified()
	{
		return getFileHandler().getLastModified();
	}
	
	public boolean isDirty()
	{
		return getFileHandler().isDirty();
	}
	
	public void refresh() throws FileNotFoundException,FileReadException
	{		
		if(getFileHandler().isClosed())
			getFileHandler().open();
			

		getContents().clear();
		if((getFileHandler() != null) && (getFileHandler().isOpened()))
		{
			getFileHandler().rewind();
		}
		
		update(getFileHandler().readArray());
	}
	
	public String getContentString()
	{
		StringBuilder sb = new StringBuilder();
		for(String line : getContentList())
		{
			sb.append(line);
			sb.append(FileHandler.NEWLINE);
		}
		return sb.toString();
	}

	public List<String> getContentList()
	{
		return new ArrayList<String>(m_contents);
	}
	
	public void update(String contents)
	{
		List<String> lines = Arrays.asList(contents.split(FileHandler.NEWLINE));
		update(lines);
	}
	
	public void update(List<String> contents)
	{
		m_contents.clear();
		m_contents.addAll(contents);
	}
	
	public void update(Integer lineNumber,String contents)
	{
		m_contents.set(lineNumber,contents);
	}
	
	public void append(String line)
	{
		m_contents.add(line);
	}
	
	public void append(List<String> lines)
	{
		m_contents.addAll(lines);
	}
	
	public boolean edit(int lineNumber,String line)
	{
		if(m_contents.size() >= lineNumber)
		{
			m_contents.set(lineNumber,line);
			return true;
		}
		//TODO: Log this
		return false;
	}
	
	public boolean remove(int lineNumber)
	{
		if(m_contents.size() >= lineNumber)
		{
			m_contents.remove(lineNumber);
			return true;
		}
		//TODO: Log this
		return false;
	}
	
	public void commit() throws FileWriteException
	{
		getFileHandler().clear();
		getFileHandler().write(m_contents);
	}
	
	private FileHandler getFileHandler()
	{
		return m_fileHandler;
	}
	
	private void setFileHandler(FileHandler handler)
	{
		m_fileHandler =  handler;
	}
	
	private List<String> getContents()
	{
		return m_contents; 
	}
	
	@Override
	public String toString()
	{
		return getContentString();
	}	
}
