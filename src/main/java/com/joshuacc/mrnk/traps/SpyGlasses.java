package com.joshuacc.mrnk.traps;

import java.util.Random;

import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.GameScheduler.Schedulers;
import com.joshuacc.mrnk.utils.GameTask;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;

public class SpyGlasses extends TrapClick {

	private String messageSurvivor;
	private String messageMurderer;
	
	public SpyGlasses()
	{
		MRTrapsConfig traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message.Survivor", "&c&lYou're being watched..");
		traps.putDefault(getName(), "Message.Murderer", "&eYou're currently spying &c%p!");
		messageSurvivor = TextFormat.colorize(traps.getString(getName(), "Message.Survivor"));
		messageMurderer = TextFormat.colorize(traps.getString(getName(), "Message.Murderer"));
	}
	
	@Override
	public int getDelay() 
	{
		return 60;
	}

	@Override
	protected boolean performClickAbility(Player player) 
	{
		Vector3 loc = player.clone();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		MRTeam team = mPlayer.getMapTeam();
		Player target = team.getSurvivors().get(new Random().nextInt(team.getSurvivors().size()));
		if(team.getSurvivors().size() == 0)
			return false;
		
		player.setGamemode(3);
		player.hidePlayer(target);
		player.sendMessage(messageMurderer.replace("%p", target.getName()));
		
		for(Player all : team.getSurvivors())
		{
			all.hidePlayer(player);
			all.getLevel().addSound(all, Sound.MOB_ELDERGUARDIAN_CURSE, 1F, 1F, all);
			all.sendTitle(messageSurvivor);
		}
		
		GameTask task = new GameTask(40);
		task.addLoopTask(() -> {
			if(target.isOnline() || target.getGamemode() == 3)
				player.teleport(target);
			else
			{
				task.getEndTask().doTask();
				task.cancel();
				MRPlayer.getMRPlayer(player).removeGameTask(task);
			}
		});
		task.addEndTask(() -> 
		{
			for(Player all : team.getPlayers())
			{
				all.showPlayer(player);
			}
			player.teleport(loc);
			player.showPlayer(target);
			player.setGamemode(0);
			if(!task.isCancelled())
				MRPlayer.getMRPlayer(player).removeGameTask(task);
		});
		mPlayer.addGameTask(task, Schedulers.TICK5);
		return true;
	}

	@Override
	public String getName() 
	{
		return "SpyGlasses";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/spy_glasses";
	}

	@Override
	public int getPrice() 
	{
		return 150;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Spy a random survivor to find where they are by &blooking at their perspective &ffor &e10 seconds!\n\n&7Cooldown: &a1 minute";
	}

	@Override
	public int getItem() 
	{
		return Item.GLASS;
	}

	@Override
	public String getTrapName() 
	{
		return "&lSpy Glasses";
	}

}
