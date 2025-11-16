package com.joshuacc.mrnk.listeners;

import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.events.GameEndEvent.WinType;
import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.events.PlayerKilledEvent;
import com.joshuacc.mrnk.files.MRGameConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.NPCHuman;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;

public class MRGameListener implements Listener {

	private MRMain main;
	private MRGameConfig game;

	public MRGameListener(MRMain main)
	{
		this.main = main;
		this.game = main.getMRGameConfig();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onDeath(EntityDamageByEntityEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player target = (Player) event.getEntity();
			if(target.getHealth() - event.getFinalDamage() < 1f) 
			{
				Server.getInstance().getPluginManager().callEvent(new PlayerKilledEvent(MRPlayer.getMRPlayer(target).getMapTeam(), target, event.getDamager() instanceof Player ? (Player) event.getDamager() : null));
				event.setCancelled(true);
			}
		}
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

	@EventHandler
	public void onKill(PlayerKilledEvent event)
	{
		MRTeam team = event.getTeam();
		Player player = event.getKilled();
		Player killer = event.getKiller();
		String reason = TextUtils.formatPlayer(killer != null ? ConfigLang.SURVIVORKILLED.toString().replace("%k", killer.getName()) : ConfigLang.SURVIVORDIE.toString(), player);

		team.addSpectator(player);
//		team.updateEntry("Players", team.getSurvivors().size()+"");
		team.updateEntry(4, team.getSurvivors().size());

		if(killer != null)
		{
			main.getMRPlayerConfig().incrementPoints(killer, game.getKillPoints());
		}

		for(Player players : team.getPlayers())
			players.sendMessage(reason);

		if(team.getSurvivors().size() == 0)
			Server.getInstance().getPluginManager().callEvent(new GameEndEvent(team, WinType.KILL_ALL));
	}

	@EventHandler
	public void onEndRound(GameEndEvent event)
	{
		MRTeam team = event.getTeam();
		Player killer = team.getKiller();
		WinType type = event.getWinType();
		String title = "";

		switch(type)
		{
		default:
		case SURVIVORS_LEAVE:
		case KILL_ALL:
			title = ConfigLang.MURDERERWIN.toString();
			main.getMRPlayerConfig().incrementPoints(killer, game.getKillPoints());
			break;
		case KILLER_LEAVE:
		case OUT_OF_TIME:
		case VEHICLE_SUCCESS:
			title = ConfigLang.SURVIVORWIN.toString();

			for(Player players : team.getSurvivors())
				main.getMRPlayerConfig().incrementPoints(players, game.getKillPoints());
			
			MRPlayer.getMRPlayer(killer).setTime(team.getMapConfig().getTimeLimit());
			break;
		}
		
		team.cancelTimer();

		for(Player player : team.getPlayers())
		{
			player.sendTitle(title, type.getSubtitle());

			if(type != WinType.KILL_ALL)
				player.sendMessage(type.getMessage());
			else
				player.sendMessage(type.getMessage(killer));
		}

		if(killer != null)
			team.addSpectator(killer);
	}
}
