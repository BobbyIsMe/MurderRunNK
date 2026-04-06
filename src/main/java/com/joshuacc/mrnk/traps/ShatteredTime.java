package com.joshuacc.mrnk.traps;

import java.util.HashMap;

import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.items.ShopItem;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.GameTask;
import com.joshuacc.mrnk.utils.TaskAct;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.Particle;

public class ShatteredTime extends TrapDrop {
	
	private final HashMap<Player, Location> positions = new HashMap<>();
	
	@Override
	public void performDropAbility(Player player) 
	{
		player.teleport(positions.get(player));
	}

	@Override
	protected boolean survivorItem() 
	{
		return true;
	}

	@Override
	protected int getParticle() 
	{
		return Particle.TYPE_CRITICAL;
	}

	@Override
	public String getName() 
	{
		return "ShatteredTime";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/shattered_time";
	}

	@Override
	public int getPrice() 
	{
		return 150;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Send the murderer &bback in time &ffrom &b2 seconds ago&f when activated!";
	}

	@Override
	public int getItem() 
	{
		return Item.CLOCK;
	}

	@Override
	public String getTrapName() 
	{
		return "&bShattered Time";
	}
	
	@EventHandler
	public void onGameStart(GameStartEvent event)
	{
		if(event.getGameAttribute() == GameAttribute.ROUND)
		{
			for(Player player : event.getTeam().getSurvivors())
			{
				for(ShopItem item : MRPlayer.getMRPlayer(player).getShopItems())
				{
					if(item.getItem().getCustomName().equals(this.getTrapItemName()))
					{
						MRTeam team = event.getTeam();
						Player killer = team.getKiller();
						positions.put(killer, team.getMurdererLocation());
						GameTask task = new GameTask(2);
						task.addLoopTask(new TaskAct() {

							@Override
							public void doTask() 
							{
								positions.put(killer, killer.getLocation());
							}
						});
						task.addEndTask(new TaskAct() {
							@Override
							public void doTask() 
							{
								positions.remove(killer);
							}
						});
						MRPlayer.getMRPlayer(team.getKiller()).addGameTask(task);
						return;
					}
				}
			}
		}
	}
}

