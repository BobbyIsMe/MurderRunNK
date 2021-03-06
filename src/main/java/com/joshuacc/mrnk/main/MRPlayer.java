package com.joshuacc.mrnk.main;

import java.util.ArrayList;
import java.util.HashMap;

import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.events.PlayerJoinGameEvent;
import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.files.MRLobbyConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.scoreboards.ScoreboardAbstract;
import com.joshuacc.mrnk.scoreboards.WaitScoreboard;
import com.joshuacc.mrnk.utils.MapState;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.particle.FloatingTextParticle;

public class MRPlayer {

	private static final HashMap<Player,MRPlayer> addPlayer = new HashMap<>();

	private Player player;
	private MRTeam mapTeam;
	private MRLobbyConfig lobby;
	private ScoreboardAbstract board;

	private ArrayList<EntityItem> itemDrops = new ArrayList<EntityItem>();

	private int time;
	private int qPts;

	private boolean hasRound;

	private MRPlayer(MRMain main, Player player, MRTeam mapTeam)
	{
		this.player = player;
		this.lobby = main.getMRLobbyConfig();
		this.mapTeam = mapTeam;
		this.board = null;
		this.time = 0;

		this.itemDrops = new ArrayList<>();

		this.hasRound = false;

		int max = mapTeam.getMapConfig().getPointsLimit();
		int i = main.getMRPlayerConfig().getPoints(player);

		this.qPts = i >= max ? max : i;

		addPlayer.put(player, this);
	}

	public void removePlayer()
	{
		addPlayer.remove(player);
	}

	public void queue(boolean restart)
	{
		if(!restart)
		{
			player.removeAllEffects();
			player.getInventory().clearAll();

			removeAllDrops();
		}

		player.teleport(lobby.getQueueLobbyLocation());
	}

	public void unqueue()
	{
		player.teleport(lobby.getMainLobbyLocation());
		removePlayer();
	}

	public void updateTime()
	{
		time++;
	}

	public void setScoreboard(ScoreboardAbstract board)
	{
		if(this.board != null)
			this.board.removeScoreboard();

		this.board = board;
		board.openScoreboard();
	}

	public void setHasRound()
	{
		this.hasRound = true;
	}

	public void addDropItem(EntityItem item)
	{
		itemDrops.add(item);
	}

	public void removeDropItem(EntityItem item)
	{
		itemDrops.remove(item);
	}

	public void removeAllDrops()
	{
		itemDrops.forEach((e) -> e.close());
		itemDrops.clear();
	}

	public static MRPlayer getMRPlayer(Player player)
	{
		return addPlayer.get(player);
	}

	public MRTeam getMapTeam()
	{
		return mapTeam;
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

	public boolean hasRound()
	{
		return hasRound;
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
			player.setNameTag(TextUtils.formatPlayer(ConfigLang.LOBBYTAG.toString(), player));
			main.getMRPlayerConfig().addPlayerData(player);
		}

		@EventHandler
		public void onInitializePlayer(PlayerLocallyInitializedEvent event) 
		{
			Player player = event.getPlayer();

			for(FloatingTextParticle particles : main.getMRLobbyConfig().getHolograms())
				player.getLevel().addParticle(particles, player);

			for(FloatingTextParticle particles : main.getMRLobbyConfig().getModesHologram())
				player.getLevel().addParticle(particles, player);

			if(lobby.getMainLobbyLocation().getLevel() != null)
				player.teleport(lobby.getMainLobbyLocation());
		}

		@EventHandler
		public void onRespond(PlayerFormRespondedEvent event)
		{	
			main.getFormUtil().handleAllResponse(event.getPlayer(), event.getFormID(), event.getWindow(), event.getResponse());
		}

		@EventHandler
		public void onJoinGame(PlayerJoinGameEvent event)
		{
			Player player = event.getPlayer();
			MRTeam team = event.getMapTeam();
			MRArenasConfig config = team.getMapConfig();
			MRPlayer mPlayer = new MRPlayer(main, player, team);

			player.sendMessage(TextUtils.format(TextUtils.formatLevel(ConfigLang.PLAYERQUEUE.toString(), team.getMapOrigin())));

			mPlayer.setScoreboard(new WaitScoreboard(player, main));
			mPlayer.queue(false);

			team.addAllPlayer(player);
			team.updateEntry("Players", team.getPlayers().size()+"", config.getMaximumPlayers()+"");
			team.messageAllPlayers(TextUtils.format(TextUtils.formatPlayer(ConfigLang.MAPNOTIFYQUEUE.toString(), player)));

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
				MRArenasConfig config = team.getMapConfig();

				if(team.onIntermission())
					team.updateEntry("Players", team.getPlayers().size()+"", config.getMaximumPlayers()+"");
				else
				{
					mPlayer.removeAllDrops();
					team.updateEntry("Players", team.getSurvivors().size()+"");
				}

				team.removePlayer(player);

				for(Player players : team.getPlayers())
					if(team.getState() == MapState.STARTED)
						players.sendMessage(TextUtils.formatPlayer(TextUtils.format(ConfigLang.PlAYERLEAVESTART.toString()), player));
					else
						players.sendMessage(TextUtils.formatPlayer(TextUtils.format(ConfigLang.PLAYERLEAVE.toString()), player));

				mPlayer.removePlayer();
			}
		}
	}
}
