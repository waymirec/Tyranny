package net.waymire.tyranny.common.message;

public interface Message
{
	public Object getSource();
	public String getTopic();
	public MessagePriority getPriority();
	public Long getSequence();
	public void setPriority(MessagePriority priority);
	public void setProperty(String key, Object value);
	public Object getProperty(String key);
}
