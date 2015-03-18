package net.waymire.tyranny.common;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RecStat {
	PENDING("P"),
	ACTIVE("A"),
	INACTIVE("I"),
	DELETED("D"),
	CONFLICT("C"),
	;
	
	private static final Map<String,RecStat> lookup =  new HashMap<String,RecStat>();
	
	static
	{
		for(RecStat recStat : EnumSet.allOf(RecStat.class))
		{
			lookup.put(recStat.toString(),recStat);
		}
	}

	private String value;

	public static RecStat lookup(String value)
	{
		return lookup.get(value);
	}
	
	private RecStat(String value)
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
}
