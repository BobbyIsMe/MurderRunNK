package com.joshuacc.mrnk.traps;

import java.util.ArrayList;

import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;

public class Nightmare extends TrapClick {

	private ArrayList<String> all = new ArrayList<>();
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
			all.add(survivor.getName());
		}

		Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {

			@Override
			public void onRun(int arg0) 
			{
				for(Player survivor : MRPlayer.getMRPlayer(player).getMapTeam().getSurvivors())
				{
					if(all.contains(survivor.getName()))
					{
						survivor.setMovementSpeed(0.1F);
						all.remove(survivor.getName());
					}
				}
				player.setMovementSpeed(0.1F);
			}

		}, 20 * 10);
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
		return "&cBlinds and slows&f all of the survivors for &b10 seconds\n\n&7Cooldown: &a1 minute";
	}

	@Override
	public Item getItem() 
	{
		return new Item(Item.WOOL, 15);
	}

	@Override
	public String getTrapName() 
	{
		return "&0&lNightmare";
	}
}
