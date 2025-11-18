package com.joshuacc.mrnk.main;

import java.util.ArrayList;
import java.util.HashMap;
import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.events.PlayerJoinGameEvent;
import com.joshuacc.mrnk.events.GameEndEvent.WinType;
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
	private MRMain main;
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
		this.main = main;
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

		setScoreboard(new WaitScoreboard(player, main));

		player.teleport(lobby.getQueueLobbyLocation());
	}

	public void unqueue()
	{
		player.removeAllEffects();
		player.getInventory().clearAll();
		player.teleport(lobby.getMainLobbyLocation());
		player.sendTip("!stop1!stop2");
		player.sendActionBar("!bar");
		removePlayer();
	}

	public void updateTime()
	{
		time++;
	}
	
	public void setTime(int time)
	{
		this.time = time;
	}

	public void setScoreboard(ScoreboardAbstract board)
	{
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
	
	public void addPoints(int qPts)
	{
		this.qPts += qPts;
		if(qPts != 0 && this.qPts != 0)
			board.updateEntry(board.getInt("Points"), qPts);
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
			
			player.removeAllEffects();
			player.sendTip("!stop1!stop2");
			player.sendActionBar("!bar");
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

			mPlayer.queue(false);

			team.addAllPlayer(player);
			team.updateScoreboardPlayerCount();
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

				team.removePlayer(player);
				
				if(team.getPlayBoard() != null)
				{
					mPlayer.removeAllDrops();

					if (team.getKiller() != null && !player.getUniqueId().equals(team.getKiller().getUniqueId()))
						team.updateEntry(team.getPlayBoard().getInt("Survivors Left"), TextUtils.formatLine(team.getPlayBoard().getString("Survivors Left-Line"), Integer.toString(team.getSurvivors().size())));
//					team.updateEntry("Players", team.getSurvivors().size()+"");

					if(team.timerGoing() && team.getSurvivors().size() == 0)
						Server.getInstance().getPluginManager().callEvent(new GameEndEvent(team, WinType.SURVIVORS_LEAVE));
				} else {
					team.updateScoreboardPlayerCount();
				}

				String reason = team.getState() == MapState.STARTED ? TextUtils.formatPlayer(TextUtils.format(ConfigLang.PlAYERLEAVESTART.toString()), player) : TextUtils.formatPlayer(TextUtils.format(ConfigLang.PLAYERLEAVE.toString()), player);

				for(Player players : team.getPlayers())
					players.sendMessage(reason);

				mPlayer.removePlayer();
			}
		}
	}
}

