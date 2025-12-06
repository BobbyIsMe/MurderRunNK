package com.joshuacc.mrnk.utils;

import java.util.ArrayList;
import java.util.Iterator;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;

public class GameScheduler {
	
	public enum Schedulers 
	{	
		TICK1(1),
		TICK5(5),
		TICK20(20);

		private final GameScheduler scheduler;
		
		Schedulers(int ticks)
		{
			this.scheduler = new GameScheduler(ticks);
		}
		
		public GameScheduler getGameScheduler()
		{
			return scheduler;
		}
	}
	
	private final ArrayList<GameTask> scheduler;
	
	public GameScheduler(int ticks)
	{
		scheduler = new ArrayList<>();
		Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() 
		{

			@Override
			public void onRun(int arg0) 
			{
				if(scheduler.isEmpty())
					return;
				
				Iterator<GameTask> tasks = scheduler.iterator();
				while(tasks.hasNext()) 
				{
					GameTask task = tasks.next();
					if (task.isCancelled()) 
					{
						if(task.getEndTask() != null)
							task.getEndTask().doTask();
						tasks.remove();
						continue;
					}

					if (task.getDelay() > 0 || task.getDelay() == -1) 
					{
						task.getLoopTask().doTask();
						
						if(task.getDelay() != -1)
						task.decrement();
					} else {
						if(task.getEndTask() != null)
							task.getEndTask().doTask();
						tasks.remove();
					}
				}
			}

		}, ticks);
	}
	
	public void addTask(GameTask task)
	{
		scheduler.add(task);
	}
	
	public void removeTask(GameTask task)
	{
		scheduler.remove(task);
	}
}
