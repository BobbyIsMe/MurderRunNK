package com.joshuacc.mrnk.perks;

import java.util.HashSet;
import java.util.UUID;

import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.main.MRPerks;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerQuitEvent;

public abstract class PerkBase extends MRPerks 
{
	public final HashSet<UUID> perkPlayers;
	
	public PerkBase()
	{
		this.perkPlayers = new HashSet<>();
	}
	
	public void addPlayer(Player player)
	{
		perkPlayers.add(player.getUniqueId());
	}
	
	public void removePlayer(Player player)
	{
		perkPlayers.remove(player.getUniqueId());
	}
	
	public boolean containsPlayer(Player player)
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		return perkPlayers.contains(player.getUniqueId()) && mPlayer != null && mPlayer.getMapTeam().timerGoing();
	}
	
	@EventHandler
	public void onGameStart(GameEndEvent event)
	{
		for(Player player : event.getTeam().getPlayers())
		{
			if(perkPlayers.contains(player.getUniqueId()))
				perkPlayers.remove(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if(perkPlayers.contains(player.getUniqueId()))
			perkPlayers.remove(player.getUniqueId());
	}
	
}
