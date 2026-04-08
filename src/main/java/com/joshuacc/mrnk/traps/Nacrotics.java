package com.joshuacc.mrnk.traps;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.potion.Effect;

public class Nacrotics extends TrapDrop implements Debuff {

	@Override
	public void performDropAbility(Player player) 
	{
		getTrapDebuff().applyDebuff(player);
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
	public int getItem() 
	{
		return Item.COAL_BLOCK;
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
		return "textures/items/nacrotics";
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

	@Override
	public TrapDebuff getTrapDebuff() 
	{
		return new TrapDebuff(new int[] {10, 2}) {

			@Override
			public void applyDebuff(Player player) 
			{
				player.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(20 * amplifiers[0]).setAmplifier(10).setVisible(false));
				player.addEffect(Effect.getEffect(Effect.WEAKNESS).setDuration(20 * amplifiers[0]).setAmplifier(amplifiers[1]).setVisible(false));
			}
			
		};
	}

}
