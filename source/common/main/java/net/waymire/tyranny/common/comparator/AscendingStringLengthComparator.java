package net.waymire.tyranny.common.comparator;

import java.io.Serializable;
import java.util.Comparator;

public class AscendingStringLengthComparator implements Comparator<String>,Serializable {
	static final long serialVersionUID = -1;
	
	public int compare(String s1, String s2)
	{
		return s1.length() - s2.length();
	}
}
