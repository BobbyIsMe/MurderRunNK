package com.joshuacc.mrnk.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.traps.TrapClick;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;

public class ItemDelay {

	private final HashMap<Player, HashMap<TrapClick, Delay>> delay;
	
	private static final ItemDelay instance = new ItemDelay();
	
	private ItemDelay()
	{
		 delay = new HashMap<>();
		 Server.getInstance().getScheduler().scheduleRepeatingTask(MRMain.getInstance(), () -> {
			 long now = System.currentTimeMillis();

			    Iterator<Map.Entry<Player, HashMap<TrapClick, Delay>>> playerIter = delay.entrySet().iterator();

			    while (playerIter.hasNext()) 
			    {
			        Map.Entry<Player, HashMap<TrapClick, Delay>> playerEntry = playerIter.next();
			        Player player =  playerEntry.getKey();
			        HashMap<TrapClick, Delay> traps = playerEntry.getValue();

			        Iterator<Map.Entry<TrapClick, Delay>> trapIterator = traps.entrySet().iterator();

			        while (trapIterator.hasNext()) 
			        {
			            Map.Entry<TrapClick, Delay> trapEntry = trapIterator.next();
			            Item item = player.getInventory().getItemInHand();
			            TrapClick trap = trapEntry.getKey();
			            Delay endTime = trapEntry.getValue();

			            if (now >= endTime.getDelay()) 
			            {
			                trapIterator.remove();
			                if(item.getCustomName().equals(trap.getTrapItemName()))
			            	{
			            		player.sendPopup(trap.getTrapItemName());
			            	}
			            } else {
			            	if(item.getCustomName().equals(trap.getTrapItemName()))
			            	{
			            		player.sendPopup(TextUtils.formatNumber(MRMain.getInstance().getMRTrapsConfig().getItemCooldownText(), endTime.getSeconds()));
			            	}
			            	endTime.decrease();
			            }
			        }

			        if (traps.isEmpty()) {
			            playerIter.remove();
			        }
			    }
		 }, 20);
	}
	
	public static ItemDelay getInstance() {
        return instance;
    }
	
	public void addCooldown(Player player, TrapClick trap)
	{
		if(trap.oneTimeUse())
			return;
		
		delay.putIfAbsent(player, new HashMap<>());
		delay.get(player).put(trap, new Delay(trap.getDelay()));
	}
	
	public boolean onCooldown(Player player, TrapClick trap)
	{
		return delay.containsKey(player) && delay.get(player).get(trap) != null;
	}
	
	public void removeAllCooldown(Player player)
	{
		delay.remove(player);
	}
}

class Delay
{
	private int seconds;
	private final long delay;
	
	public Delay(int seconds)
	{
		this.seconds = seconds;
		this.delay = System.currentTimeMillis() + (seconds * 1000);
	}
	
	public int getSeconds()
	{
		return seconds;
	}
	
	public long getDelay()
	{
		return delay;
	}
	
	public void decrease()
	{
		this.seconds--;
	}
}
