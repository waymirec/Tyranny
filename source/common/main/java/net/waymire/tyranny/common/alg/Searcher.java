package net.waymire.tyranny.common.alg;

import java.util.List;

public interface Searcher<T extends Comparable<T>> {
	public int find(List<T> list, T target);
}
