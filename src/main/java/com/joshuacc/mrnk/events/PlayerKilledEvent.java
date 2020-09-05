package com.joshuacc.mrnk.events;

import com.joshuacc.mrnk.main.MRTeam;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class PlayerKilledEvent extends Event {

	private static final HandlerList list = new HandlerList();
	private MRTeam team;
	private Player target;
	private DeathCause cause;
	
	public enum DeathCause
	{
		KILLER,
		OTHER
	}

	public PlayerKilledEvent(MRTeam team, Player target, DeathCause cause)
	{
		this.team = team;
		this.target = target;
		this.cause = cause;
	}

	public MRTeam getTeam()
	{
		return team;
	}

	public Player getKilled()
	{
		return target;
	}
	
	public DeathCause getCause()
	{
		return cause;
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
