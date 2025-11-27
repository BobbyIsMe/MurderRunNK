package com.joshuacc.mrnk.traps;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.potion.Effect;

public class Nacrotics extends TrapDrop {

	@Override
	public void performDropAbility(Player player) 
	{
		player.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(20 * 10).setAmplifier(10).setVisible(false));
		player.addEffect(Effect.getEffect(Effect.WEAKNESS).setDuration(20 * 10).setAmplifier(4).setVisible(false));
	}

	@Override
	protected boolean survivorItem() 
	{
		return true;
	}

	@Override
	protected int getParticle() 
	{
		return Particle.TYPE_SMOKE;
	}

	@Override
	public Item getItem() 
	{
		return new Item(Item.COAL_BLOCK);
	}

	@Override
	public String getTrapName() 
	{
		return "&8&lNacrotics";
	}

	@Override
	public String getName() 
	{
		return "Nacrotics";
	}

	@Override
	public String getIcon() 
	{
		return "textures/blocks/coal_block";
	}

	@Override
	public int getPrice() 
	{
		return 100;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Give the murderer &bblindness and weakness&f for &e10 seconds";
	}

}
