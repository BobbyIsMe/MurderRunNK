package com.joshuacc.mrnk.traps;

import java.util.HashMap;

import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.main.MRTraps;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;

public abstract class TrapClick extends MRTraps {

	private final static HashMap<MRPlayer, TrapClick> cooldowns = new HashMap<>();
	@Override
	public String getName()
	{
		return "r"+getTrapName();
	}
	
	@Override
	public int getCooldown()
	{
		return getDelay();
	}

	@Override
	public boolean oneTimeUse()
	{
		if(getDelay() == 0)
			return true;
		else
			return false;
	}

	@Override
	public void getAbility(Player player)
	{
		performClickAbility(player);
	}

	@Override
	public String getTrapDesc()
	{
		return "Clickable";
	}

	protected abstract int getDelay();
	protected abstract boolean performClickAbility(Player player);

	@EventHandler
	public void onClick(final PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Item item = event.getItem();
		if(item == null)
			return;
		if(item.getName().equals(getName()))
		{
			MRTeam team = MRPlayer.getMRPlayer(player).getMapTeam();
			if(team.getKiller() != null && team.getKiller().getLevel().equals(team.getMapLevel()))
			{
				if(performClickAbility(player))
				{
					if(getDelay() > 0)
//						MRMain.getInstance().addItemDelay(player, item, getDelay());
					player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
				}
			} else
				//TODO
				player.sendMessage("4The murderer has not been released yet!");
			event.setCancelled(true);
		}
	}
}
