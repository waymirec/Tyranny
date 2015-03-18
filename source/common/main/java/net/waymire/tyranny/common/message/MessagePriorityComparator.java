package net.waymire.tyranny.common.message;

import java.util.Comparator;

public class MessagePriorityComparator implements Comparator<Message>
{

	@Override
	public int compare(Message first, Message second)
	{
		if(first == null || second == null)
		{
			if(first == null && second == null)
			{
				return 0;
			}
			return first == null ? -1 : 1;
		}

		int value = first.getPriority().compareTo(second.getPriority());
		return value != 0 ? value : first.getSequence().compareTo(second.getSequence());
	}

}
