package com.joshuacc.mrnk.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.files.MRFormsTextsConfig;
import com.joshuacc.mrnk.files.MRLanguagesConfig;
import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.utils.BackupWorlds;
import com.joshuacc.mrnk.utils.MapState;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;

public class MRTeam {

	public enum MapModes
	{
		NORMAL("Normal", 103),
		ESCAPE("Escape", 104);

		private String mode;
		private static MRFormsTextsConfig FORM;
		private static MRLanguagesConfig LANG;
		private int id;

		MapModes(String mode, int id)
		{
			this.mode = mode;
			this.id = id;
		}

		public static void registerModes(MRFormsTextsConfig form, MRLanguagesConfig lang)
		{
			FORM = form;
			LANG = lang;
		}

		public String getMode()
		{
			return mode;
		}

		public String getTitle()
		{
			return TextFormat.colorize(FORM.getConfig().getString("Map-Selector."+getMode()+".Title"));
		}

		public String getDesc()
		{
			return TextFormat.colorize(FORM.getConfig().getString("Map-Selector."+getMode()+".Description"));
		}
		
		public String getHologram()
		{
			return TextFormat.colorize(LANG.getConfig().getString("Npc-Join-"+getMode()));
		}

		public int getID()
		{
			return id;
		}
	}

	private static final HashMap<MapModes, HashMap<String, MRTeam>> mapMode = new HashMap<>();

	private MapModes mode;
	private MRMain main;

	private String map;
	private String mapId;
	private String directory;

	private ArrayList<Player> allPlayers;
	private ArrayList<Player> allSurvivors;
	private ArrayList<Player> allSpectators;

	private Player killer;
	private MRArenasConfig mapConfig;
	private MRScoreboardConfig board;
	private TextUtils util;

	private boolean started;

	public MRTeam(MRMain main, String map, MapModes mode, MRArenasConfig mapConfig, int multiple)
	{
		killer = null;
		this.main = main;
		this.started = false;
		this.map = map;
		this.mapId = map+"-"+multiple;
		this.mode = mode;
		this.mapConfig = mapConfig;
		this.board = main.getMRScoreboardConfig();
		this.directory = new File(main.getFileDirectory(getMode()), mapId)+File.separator;
		allPlayers = new ArrayList<>();
		allSurvivors = new ArrayList<>();
		allSpectators = new ArrayList<>();
		util = main.getTextUtil();
		mapMode.get(mode).put(mapId, this);
		new BackupWorlds(main, getMode()).copyOverWorld(map, mapId);
		main.initWorld(directory);
	}

	public static void registerMapModes(MRFormsTextsConfig form, MRLanguagesConfig lang)
	{
		MapModes.registerModes(form, lang);
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
						officialStart();
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

	private void officialStart()
	{
		started = true;

		for(Player players : allPlayers)
			addSurvivor(players);

		updateEntry("Message", board.getString("Message-3"));
		messagePlayersDelay(util.format(ConfigLang.MAPSTARTED.toString()), 1, Sound.MOB_VILLAGER_IDLE);

		main.getServer().getScheduler().scheduleDelayedTask(new Task() {

			@Override
			public void onRun(int arg0) {
				playSoundForAll(Sound.AMBIENT_WEATHER_LIGHTNING_IMPACT);
				playSoundForAll(Sound.MOB_WITHER_SPAWN);
				playTitleAll(ConfigLang.MAPTITLESTART.toString(), ConfigLang.MAPSUBTSTART.toString(), 40);

				for(Player players : allPlayers)
				{
					players.removeEffect(Effect.BLINDNESS);
					players.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(40).setVisible(false));
				}

				main.getServer().getScheduler().scheduleDelayedTask(new Task() {

					@Override
					public void onRun(int arg0) 
					{
						for(Player players : allPlayers)
						{
							players.sendMessage(util.format(ConfigLang.MAPSELECT.toString()));
							players.getLevel().addSound(players, Sound.NOTE_BASS, 1F, 1F, players);
						}	
						selectMurderer();
					}
				}, 80);
			}	
		}, 60);
	}

	public void selectMurderer()
	{
		ArrayList<Player> randomPlayers = new ArrayList<Player>();

		for(Player players : allPlayers)
			if(!MRPlayer.getMRPlayer(players).hasRound())
				randomPlayers.add(players);

		int size = randomPlayers.size();

		if(size > 1)
			main.getServer().getScheduler().scheduleDelayedRepeatingTask(new Task() {

				int i = 0;
				Random rand = new Random();

				@Override
				public void onRun(int arg0) 
				{
					Player select = randomPlayers.get(rand.nextInt(randomPlayers.size()));
					for(Player players : allPlayers)
					{
						players.sendTitle(util.formatPlayer(ConfigLang.MAPRANDOM.toString(), select), "", 0, 20, 0);
						players.getLevel().addSound(players, Sound.RANDOM_CLICK, 1F, 1F, players);
					}

					if(i == 80)
					{
						intermission(select);
						this.cancel();
					}
					i+=2;
				}

			}, 20, 2);

		else if(size == 1)
		{
			intermission(randomPlayers.get(0));
		}

		else if(size == 0)
		{
			//TODO: end game
		}
	}

	private void intermission(Player select)
	{
		if(!select.isOnline())
		{
			messageAllPlayers(ConfigLang.KILLERLEAVE.toString());
			main.getServer().getScheduler().scheduleDelayedTask(main, () -> selectMurderer(), 40);
			return;
		}

		addKiller(select);
		for(Player players : allPlayers)
		{
			players.sendTitle("§4§l"+select.getName()+"§r", ConfigLang.MAPRANDFIN.toString(), 0, 60, 0);
			players.getLevel().addSound(players, Sound.MOB_ENDERDRAGON_GROWL, 1F, 1F, players);
		}
	}

	public void messagePlayersDelay(String message, int delay, Sound sound)
	{
		main.getServer().getScheduler().scheduleDelayedTask(new Task() {

			@Override
			public void onRun(int arg0) 
			{
				for(Player player : allPlayers)
				{
					player.sendMessage(message);
					player.getLevel().addSound(player, sound, 1F, 1F, player);
				}
			}
		}, delay * 20);
	}

	public void playSoundDelay(Sound sound, int delay)
	{
		main.getServer().getScheduler().scheduleDelayedTask(main, () -> playSoundForAll(sound), delay * 20);
	}

	public void playTitleDelay(String title, String sub, int delay)
	{
		main.getServer().getScheduler().scheduleDelayedTask(main, () -> playTitleAll(title, sub, 20), delay * 20);
	}

	public void messageAllPlayers(String message)
	{
		messageAllPlayers(allPlayers, message);
	}

	public void messageAllPlayers(ArrayList<Player> players, String message)
	{
		for(Player player : players)
			player.sendMessage(message);
	}

	public void playSoundForAll(Sound sound)
	{
		playSoundForAll(allPlayers, sound);
	}

	public void playSoundForAll(ArrayList<Player> players, Sound sound)
	{
		for(Player player : players)
			player.getLevel().addSound(player, sound, 1F, 1F, player);
	}

	public void playTitleAll(String title, String sub, int i)
	{
		playTitleAll(allPlayers, title, sub, 20, i);
	}

	public void playTitleAll(ArrayList<Player> players, String title, String sub, int i, int l)
	{
		for(Player player : players)
			player.sendTitle(title, sub, i, l, i);
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
		player.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(Integer.MAX_VALUE).setVisible(false));
		player.setNameTag(util.formatPlayerMap(ConfigLang.QUEUETAG.toString(), player, map));
		main.updatePlayerCount(mode, 1);
	}

	public void addSurvivor(Player player)
	{
		allSurvivors.add(player);
		player.setNameTag(util.formatPlayer(ConfigLang.SURVIVORTAG.toString(), player));
	}

	public void addKiller(Player player)
	{
		killer = player;
		allSurvivors.remove(player);
		player.setNameTag(util.formatPlayer(ConfigLang.KILLERTAG.toString(), player));
		MRPlayer.getMRPlayer(player).setHasRound();
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
		allSpectators.remove(player);
		main.updatePlayerCount(mode, -1);
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

	public ArrayList<Player> getSpectators()
	{
		return allSpectators;
	}
}
