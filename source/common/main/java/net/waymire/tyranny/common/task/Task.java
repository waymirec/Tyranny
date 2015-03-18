package net.waymire.tyranny.common.task;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import net.waymire.tyranny.common.GUID;

public interface Task extends Runnable
{
	public static enum IterationType { NONE, FIXED_RATE, FIXED_DELAY };
	
	public GUID getId();
	
	public void setFuture(TaskFuture future);
	public TaskFuture getFuture();
	
	public void setName(String name);
	public String getName();

	public IterationType getIterationType();
	public void setIterationType(IterationType type);
	public long getPeriod();
	public void setPeriod(long period);
	public TimeUnit getTimeUnit();
	public void setTimeUnit(TimeUnit timeUnit);
	
	public boolean hasAttribute(String key);
	public void setAttribute(String key, Object value);
	public Object getAttribute(String key);
	
	public void disable();
	public boolean isDisabled();
	public void setup();
	public void teardown();
	
	public ReadWriteLock getLock();
}
