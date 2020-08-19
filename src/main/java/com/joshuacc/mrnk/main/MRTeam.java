package com.joshuacc.mrnk.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.files.MRFormsTextsConfig;
import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.utils.BackupWorlds;
import com.joshuacc.mrnk.utils.MapState;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;

public class MRTeam {

	public enum MapModes
	{
		NORMAL("Normal", 103),
		ESCAPE("Escape", 104);

		private String mode;
		private static MRFormsTextsConfig LANG;
		private int id;

		MapModes(String mode, int id)
		{
			this.mode = mode;
			this.id = id;
		}

		public static void registerModes(MRFormsTextsConfig lang)
		{
			LANG = lang;
		}

		public String getMode()
		{
			return mode;
		}

		public String getTitle()
		{
			return TextFormat.colorize(LANG.getConfig().getString("Map-Selector."+getMode()+".Title"));
		}

		public String getDesc()
		{
			return TextFormat.colorize(LANG.getConfig().getString("Map-Selector."+getMode()+".Description"));
		}

		public int getID()
		{
			return id;
		}
	}

	private static final HashMap<MapModes, HashMap<String, MRTeam>> mapMode = new HashMap<>();

	private MapModes mode;

	private String map;
	private String mapId;
	private String directory;

	private ArrayList<Player> allPlayers;
	private ArrayList<Player> allSurvivors;
	private ArrayList<Player> allKillers;
	private ArrayList<Player> allSpectators;

	private Player killer;
	private MRArenasConfig mapConfig;
	private MRScoreboardConfig board;
	private TextUtils util;

	private boolean started;

	public MRTeam(MRMain main, String map, MapModes mode, MRArenasConfig mapConfig, int multiple)
	{
		killer = null;
		this.started = false;
		this.map = map;
		this.mapId = map+"-"+multiple;
		this.mode = mode;
		this.mapConfig = mapConfig;
		this.board = main.getMRScoreboardConfig();
		this.directory = new File(main.getFileDirectory(getMode()), mapId)+File.separator;
		allPlayers = new ArrayList<>();
		allSurvivors = new ArrayList<>();
		allKillers = new ArrayList<>();
		allSpectators = new ArrayList<>();
		util = main.getTextUtil();
		mapMode.get(mode).put(mapId, this);
		new BackupWorlds(main, getMode()).copyOverWorld(map, mapId);
		main.initWorld(directory);
	}

	public static void registerMapModes(MRFormsTextsConfig lang)
	{
		MapModes.registerModes(lang);
		for(MapModes modes : MapModes.values())
			mapMode.put(modes, new HashMap<>());
	}

	public static MRTeam getMapTeamByID(String map, int id, MapModes mode)
	{
		return mapMode.get(mode).get(map+"-"+id);
	}

	public void removeMapTeam()
	{
		mapMode.get(mode).remove(getMapId());
	}

	public void startQueueLobby()
	{
		Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() {

			int i = Integer.parseInt(board.getString("Seconds"));

			@Override
			public void onRun(int arg0) 
			{
				if(allPlayers.size() >= mapConfig.getMinimumPlayers())
				{
					updateEntry("Message", board.getString("Message-2"), i+"");
					if(i == 0)
					{
						selectRandomPlayer();
						this.cancel();
					}
				}
				else
				{
					updateEntry("Message", board.getString("Message-1"));
					this.cancel();
				}
				i--;
			}

		}, 20);
	}

	public void selectRandomPlayer()
	{
		started = true;
		updateEntry("Message", board.getString("Message-3"));
	}

	public void messageAllPlayers(String message)
	{
		for(Player players : allPlayers)
			players.sendMessage(message);
	}

	public void updateEntry(String key, String p)
	{
		updateEntry(key, p, "");
	}

	public void updateEntry(String key, String p, String p2)
	{
		for(Player players : allPlayers)
			MRPlayer.getMRPlayer(players).getScoreboard().updateEntry(key, p, p2);
	}

	public Level getMapLevel()
	{
		return Server.getInstance().getLevelByName(directory);
	}

	public String getMapOrigin()
	{
		return map;
	}

	public String getMapId()
	{
		return mapId;
	}

	public MRArenasConfig getMapConfig()
	{
		return mapConfig;
	}

	public static Collection<MRTeam> getTeams(MapModes mode)
	{
		return mapMode.get(mode).values();
	}

	public MapState getState()
	{
		if(started != true)
		{
			int size = getPlayers().size();
			if(size == mapConfig.getMaximumPlayers())
				return MapState.FULL;
			else if(size < mapConfig.getMinimumPlayers())
				return MapState.READY;
			else if(size >= mapConfig.getMinimumPlayers())
				return MapState.STARTING;
		} else
			return MapState.STARTED;
		return MapState.OFFLINE;
	}

	public String getMode()
	{
		return mode.getMode();
	}

	public TextUtils getTextUtil()
	{
		return util;
	}

	public void addAllPlayer(Player player)
	{
		allPlayers.add(player);
		player.setNameTag(util.formatPlayerMap(ConfigLang.QUEUETAG.toString(), player, map));
	}

	public void addSurvivor(Player player)
	{
		allSurvivors.add(player);
		player.setNameTag(util.formatPlayer(ConfigLang.SURVIVORTAG.toString(), player));
	}

	public void addKiller(Player player)
	{
		allSurvivors.remove(player);
		allKillers.add(player);
		killer = player;
		player.setNameTag(util.formatPlayer(ConfigLang.KILLERTAG.toString(), player));
	}

	public void addSpectator(Player player)
	{
		allSurvivors.remove(player);
		allSpectators.add(player);
		player.setNameTag(util.formatPlayer(ConfigLang.SPECTATORTAG.toString(), player));
	}

	public void removePlayer(Player player)
	{
		allPlayers.remove(player);
		allSurvivors.remove(player);
		if(player == killer)
			killer = null;
		allKillers.remove(player);
		allSpectators.remove(player);
	}

	public ArrayList<Player> getPlayers()
	{
		return allPlayers;
	}

	public ArrayList<Player> getSurvivors()
	{
		return allSurvivors;
	}

	public Player getKiller()
	{
		return killer;
	}

	public ArrayList<Player> getKillers()
	{
		return allKillers;
	}

	public ArrayList<Player> getSpectators()
	{
		return allSpectators;
	}
}
