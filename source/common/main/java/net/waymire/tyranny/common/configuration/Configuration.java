package net.waymire.tyranny.common.configuration;

import java.math.BigInteger;

public interface Configuration {

	/**
	 * Returns the Configuration value identified by <b>key</b> in <code>String</code> format.
	 * @param key	key value of the entry to retrieve.
	 * @return	<code>String</code> value of the retrieved item.
	 */
	public String getString(String path) throws ConfigPathException;
	
	/**
	 * Returns the Configuration value identified by <b>key</b> in <code>Integer</code> format.
	 * @param key	key value of the entry to retrieve.
	 * @return	<code>Integer</code> value of the retrieved item.
	 */
	public Integer getInteger(String key) throws ConfigPathException;
	
	/**
	 * Returns the Configuration value identified by <b>key</b> in <code>BigInteger</code> format.
	 * @param key	key value of the entry to retrieve.
	 * @return	<code>BigInteger</code> value of the retrieved item.
	 */
	public BigInteger getBigInteger(String key) throws ConfigPathException;
	
	/**
	 * Returns the Configuration value identified by <b>key</b> in <code>Boolean</code> format.
	 * @param key	key value of the entry to retrieve.
	 * @return	<code>Boolean</code> value of the retrieved item.
	 */
	public Boolean getBoolean(String key) throws ConfigPathException;
	
	/**
	 * Returns the Configuration value identified by <b>key</b> in <code>Byte</code> format.
	 * @param key	key value of the entry to retrieve.
	 * @return	<code>Byte</code> value of the retrieved item.
	 */
	public Byte getByte(String key) throws ConfigPathException;
	
	/**
	 * Returns the Configuration value identified by <b>key</b> in <code>Double</code> format.
	 * @param key	key value of the entry to retrieve.
	 * @return	<code>Double</code> value of the retrieved item.
	 */
	public Double getDouble(String key) throws ConfigPathException;
	
	/**
	 * Returns the Configuration value identified by <b>key</b> in <code>Float</code> format.
	 * @param key	key value of the entry to retrieve.
	 * @return	<code>Float</code> value of the retrieved item.
	 */
	public Float getFloat(String key) throws ConfigPathException;
	
	/**
	 * Returns the Configuration value identified by <b>key</b> in <code>Long</code> format.
	 * @param key	key value of the entry to retrieve.
	 * @return	<code>Long</code> value of the retrieved item.
	 */
	public Long getLong(String key) throws ConfigPathException;
	
	/**
	 * Returns the Configuration value identified by <b>key</b> in <code>Short</code> format.
	 * @param key	key value of the entry to retrieve.
	 * @return	<code>Short</code> value of the retrieved item.
	 */
	public Short getShort(String key) throws ConfigPathException;
}
