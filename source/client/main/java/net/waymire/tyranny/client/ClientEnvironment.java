package net.waymire.tyranny.client;

import net.waymire.tyranny.common.Environment;

public class ClientEnvironment 
{
    private final String root;

	private final String libraryPath;
	private final String dataPath;
	private final String binaryPath;
	private final String processorPath;
	private final String logPath;

	private final String fullLibraryPath;
	private final String fullDataPath;
	private final String fullBinaryPath;
	private final String fullProcessorPath;
	private final String fullLogPath;
	
	public ClientEnvironment(String path)
	{
        this.root = path;

		this.libraryPath = "lib";
		this.binaryPath = "bin";
		this.dataPath = "data";
		this.processorPath = "processors";
		this.logPath = "log";

        final String prefix = root.concat(Environment.getFileSeparator());
		this.fullLibraryPath = prefix.concat(libraryPath);
		this.fullDataPath = prefix.concat(dataPath);
		this.fullBinaryPath = prefix.concat(binaryPath);
		this.fullProcessorPath = prefix.concat(processorPath);
		this.fullLogPath = prefix.concat(logPath);
	}

	public String getLibraryPath() 
	{
		return libraryPath;
	}

	public String getDataPath() 
	{
		return dataPath;
	}

	public String getBinaryPath() 
	{
		return binaryPath;
	}

	public String getProcessorPath() 
	{
		return processorPath;
	}
	
	public String getLogPath() 
	{
		return logPath;
	}

	public String getFullLibraryPath() 
	{
		return fullLibraryPath;
	}

	public String getFullDataPath() 
	{
		return fullDataPath;
	}

	public String getFullBinaryPath() 
	{
		return fullBinaryPath;
	}

	public String getFullProcessorPath() 
	{
		return fullProcessorPath;
	}

	public String getFullLogPath() 
	{
		return fullLogPath;
	}
}
