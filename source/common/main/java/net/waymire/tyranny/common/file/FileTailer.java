package net.waymire.tyranny.common.file;

public interface FileTailer
{
	public void start();
	public void stop();
	public void subscribe(FileTailerListener listener);
	public void unsubscribe(FileTailerListener listener);
	public void handleFileRead(final String line);
	
}
