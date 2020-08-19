package com.joshuacc.mrnk.listeners;

import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.NPCHuman;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;

public class MRGameListener implements Listener {

	private MRMain main;

	public MRGameListener(MRMain main)
	{
		this.main = main;
	}

	@EventHandler
	public void onDamageNPC(EntityDamageByEntityEvent event)
	{
		if(event.getEntity() instanceof NPCHuman)
		{
			Player player = (Player) event.getDamager();
			if(player.getInventory().getItemInHand().getId() == Item.GOLD_AXE)
			{
				main.getMRLobbyConfig().removePlayer(event.getEntity());
				return;
			}

			String name = event.getEntity().namedTag.getString("Command");
			name = name.replaceAll("%p", player.getName());
			Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), name);

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onGameStart(GameStartEvent event)
	{
		MRTeam team = event.getTeam();

		if(event.getGameAttribute() == GameAttribute.STARTING)
			team.startQueueLobby();

		else if(event.getGameAttribute() == GameAttribute.STARTED)
		{

		}
	}
}
