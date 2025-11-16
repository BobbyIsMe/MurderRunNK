package com.joshuacc.mrnk.events;

import com.joshuacc.mrnk.main.MRTeam;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class PlayerJoinGameEvent extends Event {

	private static final HandlerList list = new HandlerList();
	private Player player;
	private MRTeam team;

	public PlayerJoinGameEvent(Player player, MRTeam team)
	{
		this.player = player;
		this.team = team;
	}

	public Player getPlayer()
	{
		return player;
	}

	public MRTeam getMapTeam()
	{
		return team;
	}
	
	public static HandlerList getHandlers()
    {
        return list;
    }

	public static HandlerList getHandlerList()
	{
		return list;
	}
}
