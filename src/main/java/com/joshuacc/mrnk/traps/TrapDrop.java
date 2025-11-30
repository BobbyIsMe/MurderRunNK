package com.joshuacc.mrnk.traps;

import com.joshuacc.mrnk.events.TrapPlacedEvent;
import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.main.MRTraps;
import com.joshuacc.mrnk.utils.ItemParticle;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;

public abstract class TrapDrop extends MRTraps {

	@Override
	public boolean oneTimeUse()
	{
		return true;
	}
	
	@Override
	public String getType()
	{
		return FormsLang.TRAPDROP.toString();
	}
	
	@Override
	public boolean isStackable() 
	{
		return true;
	}
	
	public abstract void performDropAbility(Player player);
	protected abstract boolean survivorItem();
	protected abstract int getParticle();

	@EventHandler
	public void onSpawn(ItemSpawnEvent event)
	{
		EntityItem item = event.getEntity();
		if(item.getItem().getName().equals(getTrapItemName()))
		{
			ItemParticle.getInstance().addParticle(item);
			Player owner = Server.getInstance().getPlayer(item.getItem().getNamedTag().getString("Owner"));
			MRPlayer mPlayer = MRPlayer.getMRPlayer(owner);
			if(owner != null && mPlayer != null)
			{
				mPlayer.addDropItem(item);
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		if(event.getItem().getCustomName().equals(getTrapItemName()))
		{
			MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
			if(mPlayer != null && player.getLevel().equals(mPlayer.getMapTeam().getMapLevel()))
			{
				TrapPlacedEvent trapEvent = new TrapPlacedEvent(player, this);
				Server.getInstance().getPluginManager().callEvent(trapEvent);
				if(!trapEvent.isCancelled())
				{
					Item item = event.getItem();
					item.getNamedTag().putString("Owner", player.getName());
					item.getNamedTag().putString("Trap", getName());
					item.getNamedTag().putInt("Particle", getParticle());
					item.setNamedTag(item.getNamedTag());
				} else
					event.setCancelled(true);
			} else
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClick(final PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		Item item = event.getItem();
		if(item == null)
			return;
		
		if (mPlayer != null && item.getCustomName().equals(getTrapItemName())) 
		{
			MRTeam team = MRPlayer.getMRPlayer(player).getMapTeam();
			if(mPlayer != null && player.getLevel().equals(team.getMapLevel())) 
			{
				TrapPlacedEvent trapEvent = new TrapPlacedEvent(player, this);
				Server.getInstance().getPluginManager().callEvent(trapEvent);
				if(!trapEvent.isCancelled())
				{
					Item it = item.clone();
					it.setCount(1);
					it.getNamedTag().putString("Owner", player.getName());
					it.getNamedTag().putString("Trap", getName());
					it.getNamedTag().putInt("Particle", getParticle());
					it.setNamedTag(it.getNamedTag());
					player.dropItem(it);
					player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
				}
			}
			
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickup(final InventoryPickupItemEvent event)
	{
		Player player = (Player) event.getInventory().getHolder();
		EntityItem item = event.getItem();
		if(item.getItem().getName().equals(getTrapItemName()))
		{
			doAbility(player, item);
			event.setCancelled(true);
		}
	}
	
	public void doAbility(Player player, EntityItem item)
	{
		MRTeam team = MRPlayer.getMRPlayer(player).getMapTeam();
		Player killer = team.getKiller();
		if((survivorItem() && killer != null && killer.getLevel().equals(team.getMapLevel())) || (!survivorItem() && !player.getName().equals(killer.getName())))
		{
		TrapTriggeredEvent trapEvent = new TrapTriggeredEvent(player, this);
		Server.getInstance().getPluginManager().callEvent(trapEvent);
		if(!trapEvent.isCancelled())
		{
			performDropAbility(player);
			Player owner = Server.getInstance().getPlayer(item.getItem().getNamedTag().getString("Owner"));
			if (owner != null && owner.isOnline()) 
			{
				MRPlayer mPlayer = MRPlayer.getMRPlayer(owner);
				if (mPlayer != null) 
				{
					ItemParticle.getInstance().removeParticle(item);
					mPlayer.removeDropItem(item);
					item.close();
				}
			}
		}
		}
	}
}
