package com.joshuacc.mrnk.itemAbility;

import java.util.HashMap;
import java.util.Map;

import com.joshuacc.mrnk.files.MRLobbyConfig;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;

public class ItemBuilder implements Listener {

	private static final Map<String, CustomItems> customItems = new HashMap<>();
	
	public static void registerItems(MRLobbyConfig config)
	{
		for(CustomItems item : CustomItems.values()) 
		{
			item.registerItem(config);
	        customItems.put(item.getTag(), item);
	    }
	}
	
	public enum CustomItems {
		
		SPECTATORCOMPASS(Item.get(Item.COMPASS), "Spectator-Compass", "Spectator-Compass Name", new SpectatorCompass());
		
		private final Item item;
		private final String tag;
		private final String itemName;
		private final ItemAbility itemAbility;
		
		private CustomItems(final Item item, final String tag, final String itemName, final ItemAbility itemAbility)
		{
			this.item = item;
			this.tag = tag;
			this.itemName = itemName;
			this.itemAbility = itemAbility;
		}
		
		public void registerItem(MRLobbyConfig config)
		{
			item.setCustomName(config.getConfig().getString(itemName));
			item.getNamedTag().putString("CustomItem", tag);
			item.getNamedTag().putBoolean("Droppable", false);
			item.setNamedTag(item.getNamedTag());
		}
		
		public String getTag()
		{
			return tag;
		}
		
		public void giveItem(Player player)
		{
			player.getInventory().addItem(item);
		}
		
		public void itemAbility(Player player)
		{
			itemAbility.itemAbility(player);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		String tag = event.getItem().getNamedTag().getString("CustomItem");
		if(customItems.containsKey(tag))
		{
			customItems.get(tag).itemAbility(player);
		}
	}
}
