package com.joshuacc.mrnk.perks;

import com.joshuacc.mrnk.events.PlayerKilledEvent;
import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.main.MRTraps;
import com.joshuacc.mrnk.traps.Debuff;
import com.joshuacc.mrnk.traps.TrapDrop;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.item.Item;

public class LastResort extends PerkBase 
{

	@Override
	public String getName() 
	{
		return "LastResort";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/last_resort";
	}

	@Override
	public int getPrice() 
	{
		return 500;
	}

	@Override
	public String getPerkDesc() 
	{
		return "Support the last survivors by &bactivating all of your traps to the murderer&f after you die!";
	}

	@Override
	public String getPerkName() 
	{
		return "&b&fLast Resort";
	}
	
	@EventHandler
	public void onDeath(PlayerKilledEvent event)
	{
		Player player = event.getKilled();
		Player killer = event.getKiller();
		MRTeam team = event.getTeam();
		if(this.containsPlayer(player) && killer != null && killer.getLevel().equals(team.getMapLevel()))
		{
			for(Item item : player.getInventory().getContents().values())
			{
				if(item != null && item.hasCompoundTag())
				{
					String trapName = item.getNamedTag().getString("Trap");
					if(trapName != null)
					{
						MRTraps trap = MRTraps.getTrap(trapName);
						if(trap instanceof TrapDrop && trap instanceof Debuff)
						{
							TrapDrop drop = (TrapDrop) trap;
							TrapTriggeredEvent trapEvent = new TrapTriggeredEvent(player, drop, 0);
							Server.getInstance().getPluginManager().callEvent(trapEvent);
							if(!trapEvent.isCancelled())
							{
								drop.performDropAbility(killer);
							}
						}
					}
				}
			}
		}
	}
}
