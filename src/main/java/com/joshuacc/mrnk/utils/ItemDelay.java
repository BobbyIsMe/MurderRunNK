package com.joshuacc.mrnk.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.traps.TrapClick;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;

public class ItemDelay {

	private final HashMap<MRPlayer, HashMap<TrapClick, Delay>> delay;
	
	private static final ItemDelay instance = new ItemDelay();
	
	private ItemDelay()
	{
		 delay = new HashMap<>();
		 Server.getInstance().getScheduler().scheduleRepeatingTask(MRMain.getInstance(), () -> {
			 long now = System.currentTimeMillis();

			    Iterator<Map.Entry<MRPlayer, HashMap<TrapClick, Delay>>> playerIter = delay.entrySet().iterator();

			    while (playerIter.hasNext()) 
			    {
			        Map.Entry<MRPlayer, HashMap<TrapClick, Delay>> playerEntry = playerIter.next();
			        MRPlayer mPlayer = playerEntry.getKey();
			        Player player = mPlayer.getPlayer();
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
	
	public void addCooldown(MRPlayer mPlayer, TrapClick trap)
	{
		if(trap.getDelay() == -1)
			return;
		
		delay.putIfAbsent(mPlayer, new HashMap<>());
		delay.get(mPlayer).put(trap, new Delay(trap.getDelay()));
	}
	
	public boolean onCooldown(MRPlayer mPlayer, TrapClick trap)
	{
		return delay.containsKey(mPlayer) && delay.get(mPlayer).get(trap) != null;
	}
	
	public void removeAllCooldown(MRPlayer mPlayer)
	{
		delay.remove(mPlayer);
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
