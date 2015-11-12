package net.waymire.tyranny.common.message;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageThreadFactory implements ThreadFactory {
	private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;
    
    public MessageThreadFactory(final String namePrefix) {
    	this.namePrefix = namePrefix;
    	final SecurityManager s = System.getSecurityManager();
        group = (s != null)? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }
    
	@Override
	public Thread newThread(final Runnable runnable) {
		String name = namePrefix + THREAD_NUMBER.getAndIncrement();
		Thread thread = new Thread(group, runnable, name, 0);
        
		if (!thread.isDaemon()) {
            thread.setDaemon(true);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
	}
}
