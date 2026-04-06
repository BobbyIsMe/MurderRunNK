package com.joshuacc.mrnk.perks;

import java.util.HashMap;
import java.util.UUID;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPerks;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;

public class PerkDelay {

	private static final PerkDelay instance = new PerkDelay();
	private final HashMap<MRPerks, HashMap<UUID, Long>> delays; 	
	
	private PerkDelay()
	{
		delays = new HashMap<>();
	}
	
	public static PerkDelay getInstance()
	{
		return instance;
	}
	
	public void initialize(MRPerks perk)
	{
		delays.put(perk, new HashMap<>());
	}
	
	public void putDelay(MRPerks perk, Player player, int delay, DelayIndicator indicator)
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		delays.get(perk).put(player.getUniqueId(), System.currentTimeMillis()+(delay*1000));
		if(mPlayer != null)
		{
			mPlayer.addDelayedTask(() -> {
				if(!mPlayer.getMapTeam().timerGoing())
				{
					delays.get(perk).remove(player.getUniqueId());
					return;
				}
				
				indicator.delayIndicator();
			}, delay);
		}
	}
	
	public void removeDelay(MRPerks perk, Player player)
	{
		delays.get(perk).remove(player.getUniqueId());
	}
	
	public boolean hasDelay(MRPerks perk, Player player)
	{
		return delays.get(perk).containsKey(player.getUniqueId());
	}
	
	public long secondsLeft(MRPerks perk, Player player)
	{
		return hasDelay(perk, player) ? delays.get(perk).get(player.getUniqueId())-System.currentTimeMillis() : 0;
	}
	
	public void sendDelayMessage(MRPerks perk, Player player)
	{
		player.sendMessage(MRMain.getInstance().getMRPerksConfig().getCooldownMessage(perk.getPerkName(), delays.get(perk).get(player.getUniqueId())));
	}
}

interface DelayIndicator
{
	public void delayIndicator();
}
