package com.joshuacc.mrnk.listeners;

import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.events.GameEndEvent.WinType;
import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.events.PlayerKilledEvent;
import com.joshuacc.mrnk.files.MRAreasConfig;
import com.joshuacc.mrnk.files.MRGameConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.ItemDelay;
import com.joshuacc.mrnk.utils.NPCHuman;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.utils.ConfigSection;

public class MRGameListener implements Listener {

	private MRMain main;
	private MRGameConfig game;
	private MRAreasConfig areas;

	public MRGameListener(MRMain main)
	{
		this.main = main;
		this.game = main.getMRGameConfig();
		this.areas = main.getMRAreasConfig();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onAttack(EntityDamageEvent event) 
	{
	    if(!(event.getEntity() instanceof Player)) return;

	    Player target = (Player) event.getEntity();
	    MRPlayer mPlayer = MRPlayer.getMRPlayer(target);
	    if(mPlayer == null) return;
	    
	    MRTeam team = mPlayer.getMapTeam();
	    Player killer = team.getKiller();

	    if(killer != null && target.getName().equals(mPlayer.getMapTeam().getKiller().getName())) 
	    {
	        event.setDamage(0);
	        return;
	    }

	    Player damager = null;
	    if(event instanceof EntityDamageByEntityEvent && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
	    {
	        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

	        if(e.getDamager() instanceof Player) 
	        {
	            damager = (Player) e.getDamager();
	        }
	    }

	    if (target.getLevel().equals(team.getMapLevel()) && target.getHealth() - event.getFinalDamage() < 1f) 
	    {
	        Server.getInstance().getPluginManager().callEvent(new PlayerKilledEvent(team, target, damager));
	        event.setCancelled(true);
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

//		else if(event.getGameAttribute() == GameAttribute.STARTED)
//		{
//			
//		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) 
	{
	    if(!(event.getInventory() instanceof PlayerInventory)) 
	    	return;

	    int slot = event.getSlot();
	    Player player = event.getPlayer();
	    MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
	    
	    if(mPlayer == null)
	    	return;
	    
	    Player killer = mPlayer.getMapTeam().getKiller();

	    if(killer != null && player.getName().equals(killer.getName()) && slot >= 36 && slot <= 39) 
	    {
	        event.setCancelled(true);
	    }
	}
	
	@EventHandler
	public void onTransaction(InventoryTransactionEvent event) 
	{
	    Player player = event.getTransaction().getSource();
	    MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
	    if(mPlayer == null)
	    	return;
	    
	    Player killer = mPlayer.getMapTeam().getKiller();
	    
	    if(killer != null && player.getName().equals(killer.getName()))
			for(InventoryAction action : event.getTransaction().getActions()) 
			{
				if (!(action instanceof SlotChangeAction))
					continue;

				SlotChangeAction slotAction = (SlotChangeAction) action;
				int slot = slotAction.getSlot();

				if (slot >= 36 && slot <= 39) 
				{
					event.setCancelled(true);
					break;
				}
			}
	}
	
	@EventHandler
	public void onInteractArmorStand(PlayerInteractEntityEvent event)
	{
		if(!(event.getEntity() instanceof EntityArmorStand))
			return;
		
		Player player = event.getPlayer();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(mPlayer != null && player.getLevel().equals(mPlayer.getMapTeam().getMapLevel()))
		{
			event.setCancelled(true);
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
		team.updateEntry(team.getPlayBoard().getInt("Survivors Left"), TextUtils.formatLine(team.getPlayBoard().getString("Survivors Left-Line"), Integer.toString(team.getSurvivors().size())));
		team.sendScoreboardTip();

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
	public void onBreakArena(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		ConfigSection section = areas.getArea(player);
		if(section != null && !section.getBoolean("Break Blocks") && player.getGamemode() != 1)
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreakArena(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		ConfigSection section = areas.getArea(player);
		if(section != null && !section.getBoolean("Break Blocks") && player.getGamemode() != 1)
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClickBlocks(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ConfigSection section = areas.getArea(player);
		if(section != null && event.getBlock() != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) && player.getGamemode() != 1)
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageEvent event)
	{
		if(!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		ConfigSection section = areas.getArea(player);
		if(section != null && !section.getBoolean("Damage"))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHunger(PlayerFoodLevelChangeEvent event)
	{
		Player player = (Player) event.getPlayer();
		ConfigSection section = areas.getArea(player);
		if(section != null && !section.getBoolean("Hunger"))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if(player.getInventory().getItemInHand().getName().equals(areas.getAreaItemName()) && event.getBlock() != null)
		{
			Location loc = event.getBlock().getLocation();
			if(event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				areas.addPos1(player, loc);
				player.sendMessage(TextUtils.format(ConfigLang.AREAADDEDPOS1.toString()));
			} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				areas.addPos2(player, loc);
				player.sendMessage(TextUtils.format(ConfigLang.AREAADDEDPOS2.toString()));
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if(player.getInventory().getItemInHand().getName().equals(areas.getAreaItemName()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event)
	{
		Item item = event.getItem();
		if(item.getNamedTag() != null && !item.getNamedTag().getBoolean("Droppable"))
		{
			event.setCancelled(true);
		}
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
			if(killer != null)
			main.getMRPlayerConfig().incrementPoints(killer, game.getKillPoints());
			team.addPlayerRankingByTime(killer);
			break;
		case KILLER_LEAVE:
		case OUT_OF_TIME:
		case VEHICLE_SUCCESS:
			title = ConfigLang.SURVIVORWIN.toString();

			for(Player players : team.getSurvivors())
				main.getMRPlayerConfig().incrementPoints(players, game.getKillPoints());
			
			if(killer != null)
			MRPlayer.getMRPlayer(killer).setTime(team.getMapConfig().getTimeLimit());
			break;
		}
		
		for(Player player : team.getPlayers())
		{
			MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
			player.sendTitle(title, type.getSubtitle());

			if(type != WinType.KILL_ALL)
				player.sendMessage(type.getMessage());
			else
				player.sendMessage(type.getMessage(killer));
			
			mPlayer.removeAllDrops();
			ItemDelay.getInstance().removeAllCooldown(player);
		}

		for(Player players : team.getSurvivors())
			team.addSpectator(players);
		
		team.removeAllSurvivors();
		
		if(killer != null)
			team.addSpectator(killer);
		
		team.cancelTimer();
	}
}
