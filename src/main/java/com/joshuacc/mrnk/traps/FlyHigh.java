package com.joshuacc.mrnk.traps;

import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;

public class FlyHigh extends TrapDrop {

	private String message;
	
	public FlyHigh()
	{
		MRTrapsConfig traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message", "&b&lTOBE FLY HIGH!");
		message = TextFormat.colorize(traps.getString(getName(), "Message"));
	}
	
	@Override
	public void performDropAbility(Player player) 
	{
		player.addEffect(Effect.getEffect(Effect.LEVITATION).setDuration(20 * 3).setAmplifier(20).setVisible(false));
		player.sendTitle(message);
		player.getLevel().addSound(player, Sound.MOB_BAT_TAKEOFF);
		
		MRPlayer.getMRPlayer(player).addDelayedTask(() -> 
		{
			player.setMotion(player.getDirectionVector().multiply(-2));
			player.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(20 * 3).setAmplifier(10).setVisible(false));
		}, 3);
	}

	@Override
	protected boolean survivorItem() 
	{
		return true;
	}

	@Override
	protected int getParticle() 
	{
		return Particle.TYPE_BLUE_FLAME;
	}

	@Override
	public String getName() 
	{
		return "FlyHigh";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/fly_high";
	}

	@Override
	public int getPrice() 
	{
		return 200;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Make the murderer &bsoar above the skies &fto give you time on escaping away!";
	}

	@Override
	public int getItem() 
	{
		return Item.FEATHER;
	}

	@Override
	public String getTrapName() 
	{
		return "&lFly High";
	}

}
