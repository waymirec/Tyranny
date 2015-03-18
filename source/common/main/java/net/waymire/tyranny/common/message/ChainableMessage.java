package net.waymire.tyranny.common.message;

public class ChainableMessage extends StandardMessage
{
	private Message chainedMessage;
	
	public ChainableMessage(Object source,String topic)
	{
		super(source,topic);
	}
	
	public Message getChainedMessage()
	{
		return chainedMessage;
	}
	
	public void setChainedMessage(Message message)
	{
		this.chainedMessage = message;
	}
}
