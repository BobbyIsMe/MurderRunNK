package com.joshuacc.mrnk.traps;

import java.util.HashMap;

import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.items.ShopItem;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.GameTask;
import com.joshuacc.mrnk.utils.TaskAct;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.utils.TextFormat;

public class ShatteredTime extends TrapDrop {
	
	private final HashMap<Player, Location> positions = new HashMap<>();
	private String message;
	
	public ShatteredTime()
	{
		MRTrapsConfig traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message", "&bYou were sent back in time!");
		message = TextFormat.colorize(traps.getString(getName(), "Message"));
	}
	
	@Override
	public void performDropAbility(Player player) 
	{
		player.sendMessage(message);
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
						GameTask task = new GameTask(0);
						task.addLoopTask(new TaskAct() {

							int i = 2;
							@Override
							public void doTask() 
							{
								if(i == 0)
								{
									positions.put(killer, killer.getLocation());
									i = 2;
								}
								i--;
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

