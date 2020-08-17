package com.joshuacc.mrnk.main;

import java.util.HashMap;

import com.joshuacc.mrnk.events.PlayerJoinGameEvent;
import com.joshuacc.mrnk.files.MRLobbyConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.scoreboards.WaitScoreboard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;

public class MRPlayer {

	private static final HashMap<Player,MRPlayer> addPlayer = new HashMap<>();
	
	private Player player;
	private MRTeam mapTeam;
	
	private int time;
	private int qPts;
	
	private MRPlayer(Player player, MRTeam mapTeam)
	{
		addPlayer.put(player, this);
		this.player = player;
		this.mapTeam = mapTeam;
		this.time = 0;
		this.qPts = 0;
	}
	
	public void removePlayer()
	{
		addPlayer.remove(player);
	}
	
	public void queue(Location loc)
	{
		if(loc.getLevel() != null)
		player.teleport(loc);
	}
	
	public static MRPlayer getMRPlayer(Player player)
	{
		return addPlayer.get(player);
	}
	
	public MRTeam getMapTeam()
	{
		return mapTeam;
	}
	
	public int getPlayerTime()
	{
		return time;
	}
	
	public int getPlayerQueuedPoints()
	{
		return qPts;
	}
	
	public static void registerListener(final MRMain main)
	{
		Server.getInstance().getPluginManager().registerEvents(new PlayerListener(main), main);
	}
	
	private static class PlayerListener implements Listener
	{
		private MRLobbyConfig lobby;
		private MRMain main;
		
		public PlayerListener(MRMain main)
		{
			this.main = main;
			this.lobby = main.getMRLobbyConfig();
		}
		
		@EventHandler
		public void onJoin(PlayerJoinEvent event)
		{
			Player player = event.getPlayer();
			player.setNameTag(main.getTextUtil().formatPlayer(ConfigLang.LOBBYTAG.toString(), player));
		}
		
		@EventHandler
		public void onDataPk(DataPacketReceiveEvent event) {
	        if (event.getPacket() instanceof SetLocalPlayerAsInitializedPacket) {
	            Player player = event.getPlayer();
	            
	            for(FloatingTextParticle particles : main.getMRLobbyConfig().getHolograms())
	            	player.getLevel().addParticle(particles, player);
	            
	            for(FloatingTextParticle particles : main.getMRLobbyConfig().getModesHologram())
	            	player.getLevel().addParticle(particles, player);
	            
	            if(lobby.getMainLobbyLocation().getLevel() != null)
	    			player.teleport(lobby.getMainLobbyLocation());
	        }
		}
		
		@EventHandler
		public void onRespond(PlayerFormRespondedEvent event)
		{	
			main.getFormUtil().handleAllResponse(event.getPlayer(), event.getFormID(), event.getWindow(), event.getResponse());
		}
		
		@EventHandler
		public void onJoinGame(PlayerJoinGameEvent event)
		{
			MRTeam team = event.getMapTeam();
			Player player = event.getPlayer();
			MRPlayer mPlayer = new MRPlayer(player, team);
			
			new WaitScoreboard(player, main).openScoreboard();
			mPlayer.queue(lobby.getQueueLobbyLocation());
			team.addAllPlayer(player);
		}
		
		@EventHandler
		public void onLeave(PlayerQuitEvent event)
		{
			Player player = event.getPlayer();
			if(MRPlayer.getMRPlayer(player) != null)
			{
				MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
				MRTeam team = mPlayer.getMapTeam();
				team.removePlayer(player);
				mPlayer.removePlayer();
			}
		}
	}
}
