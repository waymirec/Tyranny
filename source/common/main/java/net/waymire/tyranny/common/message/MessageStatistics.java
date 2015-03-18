package net.waymire.tyranny.common.message;

public interface MessageStatistics
{
	public int getSubscriberCount();
	public int getSubscriberCount(String topic);
	public int getPendingCount();
	public long getPublishedCount();
}
