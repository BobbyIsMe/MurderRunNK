package com.joshuacc.mrnk.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.scheduler.Task;

public class TaskDelay {
	
	private MRMain main;
	private Queue<TaskQueue> queues;
	
	public TaskDelay(MRMain main)
	{
		this.main = main;
		this.queues = new LinkedList<>();
	}
	
	public void addTask(TaskQueue task)
	{
		queues.add(task);
	}
	
	public void addTasks(ArrayList<TaskQueue> tasks)
	{
		queues.addAll(tasks);
	}
	
	public void startTasks()
	{
		main.getServer().getScheduler().scheduleDelayedRepeatingTask(main, new Task() {

			int seconds = 0;
			@Override
			public void onRun(int arg0) 
			{
				if(queues.isEmpty())
				{
					this.cancel();
					return;
				}
				
				TaskQueue task = queues.peek();
				if(task.getDelay() > seconds)
					seconds++;
				else
				{
					seconds = 0;
					task.doTask();
					queues.poll();
				}
			}
		}, 0, 20);
	}
}
