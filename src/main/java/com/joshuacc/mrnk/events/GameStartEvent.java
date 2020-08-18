package com.joshuacc.mrnk.events;

import com.joshuacc.mrnk.main.MRTeam;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class GameStartEvent extends Event {

	private static final HandlerList list = new HandlerList();
	private GameAttribute attribute;
	private MRTeam team;
	
	public enum GameAttribute
	{
		STARTING,
		STARTED
	}
	
	public GameStartEvent(GameAttribute attribute, MRTeam team)
	{
		this.attribute = attribute;
		this.team = team;
	}
	
	public GameAttribute getGameAttribute()
	{
		return attribute;
	}
	
	public MRTeam getTeam()
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
