package com.joshuacc.mrnk.main;

import java.util.HashMap;

import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.events.PlayerJoinGameEvent;
import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.files.MRLobbyConfig;
import com.joshuacc.mrnk.files.MRPlayerConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.scoreboards.ScoreboardAbstract;
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
	private MRPlayerConfig playerData;
	private ScoreboardAbstract board;
	
	private int time;
	private int qPts;
	
	private MRPlayer(MRMain main, Player player, MRTeam mapTeam)
	{
		addPlayer.put(player, this);
		this.player = player;
		this.playerData = main.getMRPlayerConfig();
		this.mapTeam = mapTeam;
		this.board = null;
		this.time = 0;
		
		int max = mapTeam.getMapConfig().getPointsLimit();
		int i = playerData.getPoints(player);
		if(i >= max)
		this.qPts = max;
		else
			this.qPts = i;
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
	
	public void setScoreboard(ScoreboardAbstract board)
	{
		if(board != null)
			board.removeScoreboard();
		
		this.board = board;
		board.openScoreboard();
	}
	
	public static MRPlayer getMRPlayer(Player player)
	{
		return addPlayer.get(player);
	}
	
	public MRTeam getMapTeam()
	{
		return mapTeam;
	}
	
	public MRPlayerConfig getPlayerConfig()
	{
		return playerData;
	}
	
	public ScoreboardAbstract getScoreboard()
	{
		return board;
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
			main.getMRPlayerConfig().addPlayerData(player);
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
			MRArenasConfig config = team.getMapConfig();
			Player player = event.getPlayer();
			MRPlayer mPlayer = new MRPlayer(main, player, team);
			
			mPlayer.setScoreboard(new WaitScoreboard(player, main));
			team.addAllPlayer(player);
			team.updateEntry("Players", team.getPlayers().size()+"", config.getMaximumPlayers()+"");
			mPlayer.queue(lobby.getQueueLobbyLocation());
			
			if(team.getPlayers().size() == config.getMinimumPlayers())
				Server.getInstance().getPluginManager().callEvent(new GameStartEvent(GameAttribute.STARTING, team));
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
