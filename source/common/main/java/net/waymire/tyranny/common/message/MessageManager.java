package net.waymire.tyranny.common.message;

import java.util.Map;

public interface MessageManager 
{
	public void start();
	public void stop();
	public void setCapacity(int capacity);
	public int getCapacity();
	public boolean isRunning();
	public void subscribe(String topic,MessageListener listener);
	public void subscribe(Map<String,MessageListener> map);
	public void unsubscribe(String topic,MessageListener listener);
	public void unsubscribe(Map<String,MessageListener> map);
	public void unsubscribe(MessageListener listener);
	public boolean publish(Message message);
	public boolean publish(Message message, boolean wait);
	public void load(Object target);
	public void unload(Object target);	
}
