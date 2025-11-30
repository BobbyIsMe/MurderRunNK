package com.joshuacc.mrnk.traps;

import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.main.MRTraps;
import com.joshuacc.mrnk.utils.ItemDelay;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;

public abstract class TrapClick extends MRTraps {
	
	@Override
	public boolean oneTimeUse()
	{
		if(getDelay() == 0)
			return true;
		else
			return false;
	}
	
	@Override
	public String getType()
	{
		return FormsLang.TRAPCLICK.toString();
	}
	
	@Override
	public boolean isStackable() 
	{
		return false;
	}

	public abstract int getDelay();
	protected abstract boolean performClickAbility(Player player);

	@EventHandler
	public void onClick(final PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		Item item = event.getItem();
		if(item == null)
			return;
		if (mPlayer != null && item.getCustomName().equals(getTrapItemName())) 
		{
			MRTeam team = MRPlayer.getMRPlayer(player).getMapTeam();
			Player killer = team.getKiller();
			if(killer != null && killer.getLevel().equals(team.getMapLevel())) 
			{
				TrapTriggeredEvent trapEvent = new TrapTriggeredEvent(player, this);
				Server.getInstance().getPluginManager().callEvent(trapEvent);
				if(!trapEvent.isCancelled())
				{
					if(!oneTimeUse())
					{
						performClickAbility(player);
						player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
					} else
					if (!ItemDelay.getInstance().onCooldown(player, this)) 
					{
						if(performClickAbility(player))
							ItemDelay.getInstance().addCooldown(player, this);
					}
				}
			} else
				player.sendMessage(ConfigLang.MURDERERNOTREL.toString());
			event.setCancelled(true);
		}
	}
}
