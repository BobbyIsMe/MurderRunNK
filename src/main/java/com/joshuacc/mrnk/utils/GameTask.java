package com.joshuacc.mrnk.utils;

public class GameTask {
	
	private TaskAct loopTask;
	private TaskAct endTask;
	private boolean cancel;
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
	
	public TaskAct getLoopTask()
	{
		return loopTask;
	}
	
	public TaskAct getEndTask()
	{
		return endTask;
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