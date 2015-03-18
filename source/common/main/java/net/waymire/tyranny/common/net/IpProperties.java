package net.waymire.tyranny.common.net;

public class IpProperties
{
	private int receiveBufferSize = IpConstants.DEFAULT_RCV_BUFFER_SIZE;
	private int sendBufferSize = IpConstants.DEFAULT_SEND_BUFFER_SIZE;
	private int keepAliveRequestInterval = IpConstants.DEFAULT_KEEPALIVE_REQUEST_INTERVAL;
	private int keepAliveRequestTimeout = IpConstants.DEFAULT_KEEPALIVE_REQUEST_TIMEOUT;
	private int idleTime = IpConstants.DEFAULT_IDLE_TIME;
	private int connectTimeout = IpConstants.DEFAULT_CONNECT_TIMEOUT;
	private boolean tcpNoDelay = true;
	private String SslKeystore = null;
	private String SslTruststore = null;
	private String SslPassword = null;
	
	public void setReceiveBufferSize(int size)
	{
		this.receiveBufferSize = size;
	}
	
	public int getReceiveBufferSize()
	{
		return receiveBufferSize;
	}
	
	public void setSendBufferSize(int size)
	{
		this.sendBufferSize = size;
	}
	
	public int getSendBufferSize()
	{
		return sendBufferSize;
	}
	
	public void setKeepAliveRequestInterval(int value)
	{
		this.keepAliveRequestInterval = value;
	}
	
	public int getKeepAliveRequestInterval()
	{
		return keepAliveRequestInterval;
	}
	
	public void setKeepAliveRequestTimeout(int value)
	{
		this.keepAliveRequestTimeout = value;
	}
	
	public int getKeepAliveRequestTimeout()
	{
		return keepAliveRequestTimeout;
	}
	
	public void setIdleTime(int value)
	{
		this.idleTime = value;
	}
	
	public int getIdleTime()
	{
		return idleTime;
	}
	
	public int getConnectTimeout()
	{
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout)
	{
		this.connectTimeout = connectTimeout;
	}

	public void setTcpNoDelay(boolean value)
	{
		this.tcpNoDelay = value;
	}
	
	public boolean getTcpNoDelay()
	{
		return tcpNoDelay;
	}

	public String getSslKeystore()
	{
		return SslKeystore;
	}

	public void setSslKeystore(String sslKeystore)
	{
		SslKeystore = sslKeystore;
	}

	public String getSslTruststore()
	{
		return SslTruststore;
	}

	public void setSslTruststore(String sslTruststore)
	{
		SslTruststore = sslTruststore;
	}

	public String getSslPassword()
	{
		return SslPassword;
	}

	public void setSslPassword(String sslPassword)
	{
		SslPassword = sslPassword;
	}
}
