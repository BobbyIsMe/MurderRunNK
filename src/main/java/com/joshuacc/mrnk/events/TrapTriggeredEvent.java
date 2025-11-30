package com.joshuacc.mrnk.events;

import com.joshuacc.mrnk.main.MRTraps;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class TrapTriggeredEvent extends Event implements Cancellable {

	private static final HandlerList list = new HandlerList();
	private final Player owner;
	private final MRTraps trap;
	
	public TrapTriggeredEvent(Player owner, MRTraps trap)
	{
		this.owner = owner;
		this.trap = trap;
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public MRTraps getTrap()
	{
		return trap;
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
