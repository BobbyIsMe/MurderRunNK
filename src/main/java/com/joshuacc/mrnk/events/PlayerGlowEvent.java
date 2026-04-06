package com.joshuacc.mrnk.events;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

public class PlayerGlowEvent extends PlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
    private final int seconds;
    private final int type;
    
    public PlayerGlowEvent(Player player, int seconds, int type)
    {
    	this.player = player;
    	this.seconds = seconds;
    	this.type = type;
    }
    
    public int getSeconds()
    {
    	return seconds;
    }
    
    public int getType()
    {
    	return type;
    }
    
    public static HandlerList getHandlers() {
        return handlers;
    }
}
