package com.joshuacc.mrnk.utils;

import java.util.HashMap;

import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.utils.GameScheduler.Schedulers;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.IntEntityData;

public class GlowEffect {

	private static final HashMap<Player, Integer> glowPlayers = new HashMap<>();
	
	public static void addGlowingEffect(Player player, int seconds, int type)
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(mPlayer != null && (!glowPlayers.containsKey(player) || seconds > glowPlayers.get(player)))
		{
			glowPlayers.put(player, seconds);
			player.setDataProperty(new IntEntityData(Entity.DATA_VARIANT, type));
			
			GameTask task = new GameTask(-1);
			task.addLoopTask(new TaskAct() {

				@Override
				public void doTask() 
				{
					
					if(glowPlayers.get(player) == 0)
					{
						if(glowPlayers.containsKey(player))
							glowPlayers.remove(player);
						player.setDataProperty(new IntEntityData(Entity.DATA_VARIANT, 0));
						if(!task.isCancelled())
						{
							MRPlayer.getMRPlayer(player).removeGameTask(task);
							task.cancel();
						}
						return;
					}
					
					glowPlayers.put(player, glowPlayers.get(player)-1);
				}
			});
			
			mPlayer.addGameTask(task, Schedulers.TICK20);
		}
		
	}
	
	public static void removeGlowingEffect(Player player)
	{
		if(glowPlayers.containsKey(player))
		{
			glowPlayers.remove(player);
			player.setDataProperty(new IntEntityData(Entity.DATA_VARIANT, 0));
		}
	}
	
	public static boolean isGlowing(Player player)
	{
		return glowPlayers.containsKey(player);
	}
}
