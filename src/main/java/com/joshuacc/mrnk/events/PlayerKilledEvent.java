package com.joshuacc.mrnk.events;

import com.joshuacc.mrnk.main.MRTeam;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class PlayerKilledEvent extends Event {

	private static final HandlerList list = new HandlerList();
	private MRTeam team;
	private Player target;
	private Player killer;

	public PlayerKilledEvent(MRTeam team, Player target, Player killer)
	{
		this.team = team;
		this.target = target;
		this.killer = killer;
	}

	public MRTeam getTeam()
	{
		return team;
	}

	public Player getKilled()
	{
		return target;
	}

	public Player getKiller()
	{
		return killer;
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
