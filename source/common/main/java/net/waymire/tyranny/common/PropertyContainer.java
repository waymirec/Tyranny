package net.waymire.tyranny.common;

public interface PropertyContainer<K> {
	public void setProperty(String key, K value);
	public K getProperty(String key);
	public boolean hasProperty(String key);
}
