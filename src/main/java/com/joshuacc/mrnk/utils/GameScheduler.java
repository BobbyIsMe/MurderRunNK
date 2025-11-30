package com.joshuacc.mrnk.utils;

import java.util.ArrayList;
import java.util.Iterator;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;

public class GameScheduler {
	
	public enum Schedulers 
	{	
		TRAPS(20);

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
				Iterator<GameTask> tasks = scheduler.iterator();
				while(tasks.hasNext()) 
				{
					GameTask task = tasks.next();
					if (task.isCancelled()) 
					{
						tasks.remove();
						continue;
					}

					if (task.getDelay() > 0 || task.getDelay() == -1) 
					{
						task.getLoopTask().doTask();
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
