package com.joshuacc.mrnk.traps;

import com.joshuacc.mrnk.utils.GlowEffect;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.potion.Effect;

public class BearTrap extends TrapDrop 
{

	@Override
	public void performDropAbility(Player player) 
	{
		player.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(20 * 5).setAmplifier(10).setVisible(false));
		player.addEffect(Effect.getEffect(Effect.JUMP_BOOST).setDuration(20 * 5).setAmplifier(150).setVisible(false));
		GlowEffect.addGlowingEffect(player, 5, 2);
	}

	@Override
	protected boolean survivorItem() 
	{
		return false;
	}

	@Override
	protected int getParticle() 
	{
		return Particle.TYPE_INK;
	}

	@Override
	public String getName() 
	{
		return "BearTrap";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/beartrap";
	}

	@Override
	public int getPrice() 
	{
		return 100;
	}

	@Override
	public String getTrapDesc() 
	{
		return "&bStun the survivor &ffor &e10 seconds&f when they touch the bear trap!";
	}

	@Override
	public int getItem() 
	{
		return Item.CHAIN;
	}

	@Override
	public String getTrapName() 
	{
		return "&cBear Trap";
	}

}
