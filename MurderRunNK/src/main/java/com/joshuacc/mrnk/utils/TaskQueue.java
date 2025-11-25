package com.joshuacc.mrnk.utils;

public abstract class TaskQueue
{
	private final int delay;
	
	public TaskQueue(int delay)
	{
		this.delay = delay;
	}
	
	public abstract void doTask();
	
	public int getDelay()
	{
		return delay;
	}
}