package com.joshuacc.mrnk.events;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class PlayerLeaveGameEvent extends Event {

	private static final HandlerList list = new HandlerList();
	private Player player;

	public PlayerLeaveGameEvent(Player player)
	{
		this.player = player;
	}

	public Player getPlayer()
	{
		return player;
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
