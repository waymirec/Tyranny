package net.waymire.tyranny.common.task;

import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.util.ExceptionUtil;

public class TaskWrapper extends StandardTask
{
	protected final Task task;
	
	protected TaskWrapper(Task task)
	{
		this.task = task;
	}

	@Override
	public void execute()
	{
		
	}
	@Override
	public void run()
	{
		if(!this.isDisabled())
		{
			if(LogHelper.isTraceEnabled(this))
			{
				LogHelper.trace(this, "Executing Task [{0}].", this.getName());
			}
			
			try
			{
				beforeRun();
				task.run();
				afterRun();
			}
			catch(Exception ex)
			{
				LogHelper.warning(this, "Exception Executing Task [{0}]: {1}", this.getName(), ExceptionUtil.getStackTrace(ex));
				ex.printStackTrace();
			}
			
			if(LogHelper.isTraceEnabled(this))
			{
				LogHelper.trace(this, "Finished Executing Task [{0}].", this.getName());
			}
		}
		else
		{
			if(LogHelper.isTraceEnabled(this))
			{
				LogHelper.trace(this, "%s.run() Called On Disabled Task [{0}].",this.getClass().getName(),this.getName());
			}
		}
	}

	protected void beforeRun()
	{
	}
	
	protected void afterRun()
	{
	}
}
