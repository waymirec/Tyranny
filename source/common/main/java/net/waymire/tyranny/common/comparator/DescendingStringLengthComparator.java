package net.waymire.tyranny.common.comparator;

import java.io.Serializable;
import java.util.Comparator;

public class DescendingStringLengthComparator implements Comparator<String>,Serializable {
	static final long serialVersionUID = -1;
	
	public int compare(String s1, String s2)
	{
		return s2.length() - s1.length();
	}

}
