package com.joshuacc.mrnk.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.joshuacc.mrnk.events.PlayerGlowEvent;
import com.joshuacc.mrnk.utils.GameScheduler.Schedulers;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.IntEntityData;

public class GlowEffect {

	private static final HashMap<Entity, Integer> glowPlayers = new HashMap<>();
	private static final GlowEffect instance = new GlowEffect();
	
	private GlowEffect()
	{
		GameTask task = new GameTask(-1);
		 task.addLoopTask(new TaskAct() {

				@Override
				public void doTask() 
				{
					Iterator<Map.Entry<Entity, Integer>> trapIterator = glowPlayers.entrySet().iterator();

			        while (trapIterator.hasNext()) 
			        {
			        	Map.Entry<Entity, Integer> trapEntry = trapIterator.next();
			        	Entity player = trapEntry.getKey();
			        	int delay = trapEntry.getValue();
			        	if(delay == 0)
						{
							trapIterator.remove();
							player.setDataProperty(new IntEntityData(Entity.DATA_VARIANT, 0));
						} else
							trapEntry.setValue(delay-1);
			        }
				}
			});
		 Schedulers.TICK20.getGameScheduler().addTask(task);
	}
	
	public static GlowEffect getInstance() {
        return instance;
    }
	
	public void addGlowingEffect(Entity player, int seconds, int type)
	{
		if((!glowPlayers.containsKey(player) || seconds > glowPlayers.get(player)))
		{
			if(player instanceof Player)
			{
				PlayerGlowEvent event = new PlayerGlowEvent((Player) player, seconds, type);
				Server.getInstance().getPluginManager().callEvent(event);
				
				if(event.isCancelled())
					return;
			}
			glowPlayers.put(player, seconds);
			player.setDataProperty(new IntEntityData(Entity.DATA_VARIANT, type));
		}
	}
	
	public void removeGlowingEffect(Player player)
	{
		if(glowPlayers.containsKey(player))
		{
			glowPlayers.remove(player);
			player.setDataProperty(new IntEntityData(Entity.DATA_VARIANT, 0));
		}
	}
	
	public boolean isGlowing(Player player)
	{
		return glowPlayers.containsKey(player);
	}
}
