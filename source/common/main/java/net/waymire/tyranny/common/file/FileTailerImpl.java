package net.waymire.tyranny.common.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.task.StandardTask;
import net.waymire.tyranny.common.task.Task;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;

public class FileTailerImpl extends TailerListenerAdapter implements FileTailer
{
	private static final Integer DEFAULT_DELAY = 500;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final Set<FileTailerListener> listeners = new HashSet<FileTailerListener>();
	private final List<String> lines = new ArrayList<>();
	
	private File file;
	private Tailer tailer;
	private Future<?> tailerFuture = null;
	private TaskFuture handlerFuture = null;
	
	public FileTailerImpl(String logFilePath)
	{
		this(new File(logFilePath));
	}
	
	public FileTailerImpl(File logFile)
	{
		this.setFile(logFile);
	}
	
	public FileTailerImpl()
	{
		
	}
	
	public void setFile(String file)
	{
		setFile(new File(file));
	}
	
	public void setFile(File file)
	{
		this.file = file;
		this.tailer = new Tailer(file, this, DEFAULT_DELAY);
	}
	
	public File getFile()
	{
		return file;
	}
	
	public String getFilepath()
	{
		return file == null ? "" : file.getAbsolutePath();
	}
	
	@Override
	public void start()
	{
		if(running.get())
		{
			throw new IllegalStateException(String.format("%s.start() called while started.",this.getClass().getName()));
		}
		if(tailer == null)
		{
			throw new IllegalStateException("no file defined.");
		}
		
		LogHelper.info(this, "Starting Tailing Of Log File [{0}].", this.getFilepath());
		
		TaskManager taskManager = AppRegistry.getInstance().retrieve(TaskManager.class);
		lock.writeLock().lock();
		try
		{
			running.set(true);
			
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "Starting File Tailer.");
			}
			
			tailerFuture = Executors.newCachedThreadPool().submit(tailer);

			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this, "Starting File Tailer Handler Task.");
			}
			
			Task handlerTask = createHandlerTask();
			handlerFuture = taskManager.scheduleWithFixedDelay(handlerTask, 0, 500, TimeUnit.MILLISECONDS);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void stop()
	{
		if(!running.get())
		{
			throw new IllegalStateException(String.format("%s.stop() called while stopped.",this.getClass().getName()));
		}
		
		LogHelper.info(this, "Stopping Tailing Of Log File [{0}].", this.getFilepath());
		
		lock.writeLock().lock();
		try
		{
			running.set(false);
			AppRegistry.getInstance().retrieve(TaskManager.class).cancel(handlerFuture);
			tailer.stop();
			tailerFuture.cancel(true);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void subscribe(FileTailerListener listener)
	{
		LogHelper.debug(this, "Subscribing Listener [{0}] To File Tailer [{1}].", listener.toString(), this.getClass().getName());
		lock.writeLock().lock();
		try
		{
			listeners.add(listener);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public void unsubscribe(FileTailerListener listener)
	{
		LogHelper.debug(this, "Unsubscribing Listener [{0}] From File Tailer [{1}].", listener.toString(), this.getClass().getName());
		lock.writeLock().lock();
		try
		{
			listeners.remove(listener);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void handleFileRead(final String line) 
	{
		lock.writeLock().lock();
		try
		{
			lines.add(line);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public void handle(String line)
	{
		this.handleFileRead(line);
	}
	
	private Task createHandlerTask()
	{
		Task task = new StandardTask()
		{
			@Override
			public void execute()
			{
				List<String> content = null;
				lock.writeLock().lock();
				try
				{
					content = new ArrayList<>(lines);
					lines.clear();
				
					for(FileTailerListener listener : listeners)
					{
						listener.onFileTailRead(content);
					}
				}
				finally
				{
					lock.writeLock().unlock();
				}
			}
		};
		task.setName("FileTailer_handlerTask");
		return task;
	}
}
