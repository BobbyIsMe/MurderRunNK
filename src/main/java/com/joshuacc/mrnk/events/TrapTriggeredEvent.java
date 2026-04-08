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
	private int delay;
	
	public TrapTriggeredEvent(Player owner, MRTraps trap, int delay)
	{
		this.owner = owner;
		this.trap = trap;
		this.delay = delay;
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public MRTraps getTrap()
	{
		return trap;
	}
	
	public int getDelay()
	{
		return delay;
	}
	
	public void setDelay(int delay)
	{
		this.delay = delay;
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
