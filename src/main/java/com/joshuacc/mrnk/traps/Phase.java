package com.joshuacc.mrnk.traps;

import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.utils.ItemParticle;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.potion.Effect;

public class Phase extends TrapClick {

	@Override
	public int getDelay() 
	{
		return 120;
	}

	@Override
	protected boolean performClickAbility(Player player) 
	{
		player.setGamemode(3);
		player.setMotion(player.getDirectionVector().multiply(2));
		
		ItemParticle.getInstance().addParticle(player, Particle.TYPE_EVAPORATION, 20);
		player.getLevel().addSound(player, Sound.MOB_WITHER_SHOOT, 1F, 2F);
		MRPlayer.getMRPlayer(player).addDelayedTask(() -> {
			player.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(20 * 3).setAmplifier(50));
			player.addEffect(Effect.getEffect(Effect.NAUSEA).setDuration(20 * 8).setAmplifier(100));
			player.setGamemode(0);
		}, 1);
		return true;
	}

	@Override
	public String getName() 
	{
		return "Phase";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/phase";
	}

	@Override
	public int getPrice() 
	{
		return 200;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Look at a wall and &bphase through the blocks &fin front of you!\n\n&7Cooldown: &a2 minutes";
	}

	@Override
	public int getItem() 
	{
		return Item.PAPER;
	}

	@Override
	public String getTrapName() 
	{
		return "&f&lPhase";
	}

}
