package com.joshuacc.mrnk.perks;

import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.main.MRTraps;
import com.joshuacc.mrnk.traps.Debuff;
import com.joshuacc.mrnk.traps.TrapClick;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;

public class WhateverItTakes extends PerkBase {

	@Override
	public String getName() 
	{
		return "WhateverItTakes";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/whatever_it_takes";
	}

	@Override
	public int getPrice() 
	{
		return 750;
	}

	@Override
	public String getPerkDesc() 
	{
		return "&fHunt down the survivors with &bdecrease in all cooldowns by 25%&f but at the cost of &bbeing vulnerable to survivor traps by 20%.";
	}

	@Override
	public String getPerkName() 
	{
		return "&f&lWhatever It Takes";
	}
	
	@EventHandler
	public void onTrapTrigger(TrapTriggeredEvent event)
	{
		Player player = event.getOwner();
		if(this.containsPlayer(player))  
		{
			MRTraps trap = event.getTrap();
			if(event.getTrap() instanceof TrapClick)
			{
				int delay = event.getDelay();
				event.setDelay((int) (delay - (delay * .25)));
			}
			
			if(MRTraps.isSurvivorTrap(trap.getName()) && trap instanceof Debuff)
			{
				Debuff debuffTrap = (Debuff) trap;
				debuffTrap.getTrapDebuff().setAmplifiers(.20);
			}
		}
	}

}
