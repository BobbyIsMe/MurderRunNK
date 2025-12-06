package com.joshuacc.mrnk.utils;

import com.joshuacc.mrnk.utils.GameScheduler.Schedulers;

public class GameTask {
	
	private TaskAct loopTask;
	private TaskAct endTask;
	private boolean cancel;
	private Schedulers scheduler;
	private int delay;
	
	public GameTask(int delay)
	{
		loopTask = null;
		endTask = null;
		this.delay = delay;
	}
	
	public void addLoopTask(TaskAct loopTask)
	{
		this.loopTask = loopTask;;
	}
	
	public void addEndTask(TaskAct endTask)
	{
		this.endTask = endTask;
	}
	
	public void setScheduler(Schedulers scheduler)
	{
		this.scheduler = scheduler;
	}
	
	public TaskAct getLoopTask()
	{
		return loopTask;
	}
	
	public TaskAct getEndTask()
	{
		return endTask;
	}
	
	public Schedulers getScheduler()
	{
		return scheduler;
	}
	
	public void decrement()
	{
		delay--;
	}
	
	public int getDelay()
	{
		return delay;
	}
	
	public boolean isCancelled()
	{
		return cancel;
	}
	
	public void cancel()
	{
		cancel = true;
	}
}