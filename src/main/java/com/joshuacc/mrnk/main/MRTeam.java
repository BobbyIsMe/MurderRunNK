package com.joshuacc.mrnk.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.utils.BackupWorlds;
import com.joshuacc.mrnk.utils.MapModes;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;

public class MRTeam {

	private static final HashMap<String, MRTeam> normalMode = new HashMap<>();
	private static final HashMap<String, MRTeam> escapeMode = new HashMap<>();

	private String map;
	private String mapId;
	private String mode;
	private String directory;

	private ArrayList<Player> allPlayers;
	private ArrayList<Player> allSurvivors;
	private ArrayList<Player> allKillers;
	private ArrayList<Player> allSpectators;

	private Player killer;
	private MRArenasConfig mapConfig;
	private TextUtils util;

	private boolean started;

	public MRTeam(MRMain main, String map, String mode, MRArenasConfig mapConfig, int multiple)
	{
		killer = null;
		this.started = false;
		this.map = map;
		this.mapId = map+"-"+multiple;
		this.mode = mode;
		this.mapConfig = mapConfig;
		this.directory = new File(main.getFileDirectory(mode), mapId)+File.separator;
		allPlayers = new ArrayList<>();
		allSurvivors = new ArrayList<>();
		allKillers = new ArrayList<>();
		allSpectators = new ArrayList<>();
		util = main.getTextUtil();
		if(mode == "Normal")
			normalMode.put(mapId, this);
		else if(mode == "Escape")
			escapeMode.put(mapId, this);
		new BackupWorlds(main, mode).copyOverWorld(map, mapId);
		main.initWorld(directory);
	}

	public static MRTeam getMapTeamByID(String level, int id, String type)
	{
		if(type == "Normal")
			return normalMode.get(level+"-"+id);
		else if(type == "Escape")
			return escapeMode.get(level+"-"+id);
		return null;
	}

	public void removeMapTeam()
	{
		if(mode == "Normal")
			normalMode.remove(getMapId());
		else if(mode == "Escape")
			escapeMode.remove(getMapId());
	}

	public void startCountdown(Player player)
	{

	}

	public void startQueueLobby()
	{

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
	
	public static Collection<MRTeam> getNormalTeams()
	{
		return normalMode.values();
	}
	
	public static Collection<MRTeam> getEscapeTeams()
	{
		return escapeMode.values();
	}

	public MapModes getState()
	{
		if(started != true)
		{
			int size = getPlayers().size();
			if(size == mapConfig.getMaximumPlayers())
				return MapModes.FULL;
			else if(size < mapConfig.getMinimumPlayers())
				return MapModes.READY;
			else if(size >= mapConfig.getMinimumPlayers())
				return MapModes.STARTING;
		} else
			return MapModes.STARTED;
		return MapModes.OFFLINE;
	}

	public String getMode()
	{
		return mode;
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
