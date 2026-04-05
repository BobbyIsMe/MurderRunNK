package com.joshuacc.mrnk.perks;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.potion.Effect;

public class Adrenaline extends PerkBase 
{

	@Override
	public String getName() 
	{
		return "Adrenaline";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/adrenaline";
	}

	@Override
	public int getPrice() 
	{
		return 300;
	}

	@Override
	public String getPerkDesc() 
	{
		return "Get a rush of adrenaline when the murderer hits you &bto gain speed for 1 second!";
	}

	@Override
	public String getPerkName() 
	{
		return "&f&lAdrenaline";
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event)
	{
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player)
		{
			Player target = (Player) event.getEntity();
			if(this.containsPlayer(target))
			{
				target.addEffect(Effect.getEffect(Effect.SPEED).setDuration(20).setAmplifier(2));
			}
		}
	}
}
