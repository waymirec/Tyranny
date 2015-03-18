package net.waymire.tyranny.common.alg;

import java.util.List;

public interface Sorter<T extends Comparable<T>> {
	public List<T> sort(List<T> list);
}
