package com.joshuacc.mrnk.traps;

import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;

public class Nightmare extends TrapClick {

	private String message;
	
	public Nightmare()
	{
		MRTrapsConfig traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message", "&0&kiii &r&0&lGoodnight... &r&0&kiii");
		message = TextFormat.colorize(traps.getString(getName(), "Message"));
	}
	
	@Override
	public int getDelay() 
	{
		return 60;
	}

	@Override
	protected boolean performClickAbility(Player player) 
	{
		player.setMovementSpeed(0.2F);
		for(Player survivor : MRPlayer.getMRPlayer(player).getMapTeam().getSurvivors())
		{
			survivor.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(20 * 10).setAmplifier(10));
			survivor.setMovementSpeed(0.05F);
			survivor.getLevel().addSound(survivor, Sound.AMBIENT_CAVE);
			survivor.sendTitle(message);
		}
		
		MRPlayer.getMRPlayer(player).addDelayedTask(() -> {
			for(Player all : MRPlayer.getMRPlayer(player).getMapTeam().getPlayers())
			{
				all.setMovementSpeed(0.1F);
			}
		}, 10);
		return true;
	}

	@Override
	public String getName() 
	{
		return "Nightmare";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/nightmare";
	}

	@Override
	public int getPrice() 
	{
		return 100;
	}

	@Override
	public String getTrapDesc() 
	{
		return "&cBlinds and slows&f all of the survivors for &e10 seconds\n\n&7Cooldown: &a1 minute";
	}

	@Override
	public int getItem() 
	{
		return Item.WOOL;
	}
	
	@Override
	public int getMeta()
	{
		return 15;
	}

	@Override
	public String getTrapName() 
	{
		return "&0&lNightmare";
	}
}
