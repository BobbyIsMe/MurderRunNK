package com.joshuacc.mrnk.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.events.GameEndEvent.WinType;
import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.files.MRFormsTextsConfig;
import com.joshuacc.mrnk.files.MRLanguagesConfig;
import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.scoreboards.PlayScoreboard;
import com.joshuacc.mrnk.utils.BackupWorlds;
import com.joshuacc.mrnk.utils.MapState;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;

public class MRTeam {

	public enum MapModes
	{
		NORMAL("Normal", "Kill all players before the time limit reaches to win your round!", "&6» &a&lNormal Mode Maps", 103),
		ESCAPE("Escape", "Kill all players before they fix the vehicle for their escape to win your round!", "&6» &a&lEscape Mode Maps", 104);

		private static MRFormsTextsConfig FORM;
		private static MRLanguagesConfig LANG;

		private String mode;
		private String desc;
		private String set;

		private int id;

		MapModes(String mode, String desc, String set, int id)
		{
			this.mode = mode;
			this.desc = desc;
			this.set = set;
			this.id = id;
		}

		public static void loadAllModeMaps(MRMain main, String maps, MRArenasConfig config)
		{
			if(config.isMapEnabled())
			{
				for(int i = 1; i <= config.getConfig().getInt(maps+".Normal Multiples"); i++)
					new MRTeamNormal(main, maps, config, i);
				for(int i = 1; i <= config.getConfig().getInt(maps+".Escape Multiples"); i++)
					new MRTeamEscape(main, maps, config, i);
			}
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

		public String getDescription()
		{
			return desc;
		}

		public String getSetting()
		{
			return set;
		}

		public String getTitle()
		{
			return TextFormat.colorize(FORM.getConfig().getString("Map-Selector."+getMode()+".Title"));
		}

		public String getDesc()
		{
			return TextFormat.colorize(FORM.getConfig().getString("Map-Selector."+getMode()+".Description"));
		}

		public String getMapMultiples()
		{
			return TextFormat.colorize(FORM.getConfig().getString("Settings-Texts."+getMode()));
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
	private TaskHandler task;
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

	private boolean started;

	public MRTeam(MRMain main, String map, MapModes mode, MRArenasConfig mapConfig, int multiple)
	{
		killer = null;
		task = null;
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

	public void cancelTimer()
	{
		task.cancel();
		task = null;
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
		messagePlayersDelay(TextUtils.format(ConfigLang.MAPSTARTED.toString()), 1, Sound.MOB_VILLAGER_IDLE);

		main.getServer().getScheduler().scheduleDelayedTask(new Task() {

			@Override
			public void onRun(int arg0) {

				for(Player players : allPlayers)
				{
					playSoundPlayer(players, Sound.AMBIENT_WEATHER_LIGHTNING_IMPACT);
					playSoundPlayer(players, Sound.MOB_WITHER_SPAWN);
					players.sendTitle(ConfigLang.MAPTITLESTART.toString(), ConfigLang.MAPSUBTSTART.toString(), 20, 40, 20);

					players.removeEffect(Effect.BLINDNESS);
					players.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(40).setVisible(false));
				}

				main.getServer().getScheduler().scheduleDelayedTask(new Task() {

					@Override
					public void onRun(int arg0) 
					{
						playSoundMessage(TextUtils.format(ConfigLang.MAPSELECT.toString()), Sound.NOTE_BASS);
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
						players.sendTitle(TextUtils.formatPlayer(ConfigLang.MAPRANDOM.toString(), select), "", 0, 20, 0);
						playSoundPlayer(players, Sound.RANDOM_CLICK);
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
			playSoundPlayer(players, Sound.MOB_ENDERDRAGON_GROWL);
		}

		intermissionCount(40, () -> releaseMurderer(), allPlayers, ConfigLang.INTERMISSION.toString(), ConfigLang.INTERCOUNT.toString());
	}

	private void releaseMurderer()
	{
		for(Player player: allPlayers)
			MRPlayer.getMRPlayer(player).setScoreboard(new PlayScoreboard(player, main));

		for(Player survivor : allSurvivors)
			survivor.teleport(mapConfig.getSurvivorLocation(getMapLevel()));

		intermissionCount(20, () -> {

			killer.teleport(mapConfig.getMurdererLocation(getMapLevel()));
			playSoundMessage(TextUtils.format(ConfigLang.RELEASEMURD.toString()), Sound.BLOCK_END_PORTAL_SPAWN);
			startMurdererTimer();

		}, allSurvivors, ConfigLang.MURDERANNOUNCE.toString(), ConfigLang.MURDCOUNT.toString());
	}

	private void startMurdererTimer()
	{
		task = Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(new Task() {

			MRPlayer k = MRPlayer.getMRPlayer(killer);
			int time = mapConfig.getTimeLimit();

			@Override
			public void onRun(int arg0) 
			{
				if(task == null)
					return;

				if(killer == null)
				{
					for(Player player : allPlayers)
					{
						player.sendMessage(TextUtils.format(ConfigLang.KILLERLEAVE.toString()));
						player.teleport(main.getMRLobbyConfig().getQueueLobbyLocation());
					}

					selectMurderer();
					cancelTimer();
					return;
				}

				k.updateTime();

				int i = k.getPlayerTime();

				updateEntry("Time", TextUtils.getTimeFormat(i));

				if(i == time)
				{	
					Server.getInstance().getPluginManager().callEvent(new GameEndEvent(k.getMapTeam(), WinType.OUT_OF_TIME));
					cancelTimer();
					return;
				}
			}

		}, 20, 20);
	}

	private void intermissionCount(int delay, FinishTask task, ArrayList<Player> player,String announce, String countdown)
	{
		main.getServer().getScheduler().scheduleDelayedRepeatingTask(new Task() {

			int i = mapConfig.getPreparingTime();
			int time = mapConfig.getPreparingTime();

			@Override
			public void onRun(int arg0) 
			{
				if(killer == null)
				{
					messageAllPlayers(TextUtils.format(ConfigLang.KILLERLEAVE.toString()));
					selectMurderer();
					this.cancel();
					return;
				}

				if(i == 0)
				{
					task.finish();
					this.cancel();
					return;
				}

				if(i == time)
					playSoundMessage(player, TextUtils.format(TextUtils.formatNumber(announce, i)), Sound.RANDOM_ANVIL_USE);

				else if(i % 60 == 0 || i == 15 || i == 10 || i <= 5)
					playSoundMessage(TextUtils.format(TextUtils.formatNumber(countdown, i)), Sound.RANDOM_CLICK);

				i--;
			}

		}, delay, 20);
	}

	public void messagePlayersDelay(String message, int delay, Sound sound)
	{
		main.getServer().getScheduler().scheduleDelayedTask(main, () -> playSoundMessage(message, sound), delay * 20);
	}

	public void playSoundMessage(String message, Sound sound)
	{
		playSoundMessage(allPlayers, message, sound);
	}

	public void playSoundMessage(ArrayList<Player> players, String message, Sound sound)
	{
		for(Player player : players)
		{
			player.sendMessage(message);
			playSoundPlayer(player, sound);
		}
	}

	public void playSoundPlayer(Player player, Sound sound)
	{
		player.getLevel().addSound(player, sound, 1F, 1F, player);
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
			playSoundPlayer(player, sound);
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

	public void addAllPlayer(Player player)
	{
		allPlayers.add(player);
		player.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(Integer.MAX_VALUE).setVisible(false));
		player.setNameTag(TextUtils.formatPlayerMap(ConfigLang.QUEUETAG.toString(), player, map));
		main.updatePlayerCount(mode, 1);
	}

	public void addSurvivor(Player player)
	{
		allSurvivors.add(player);
		player.setNameTag(TextUtils.formatPlayer(ConfigLang.SURVIVORTAG.toString(), player));
	}

	public void addKiller(Player player)
	{
		killer = player;
		allSurvivors.remove(player);
		player.setNameTag(TextUtils.formatPlayer(ConfigLang.KILLERTAG.toString(), player));
		MRPlayer.getMRPlayer(player).setHasRound();
	}

	public void addSpectator(Player player)
	{
		allSurvivors.remove(player);
		allSpectators.add(player);
		player.setNameTag(TextUtils.formatPlayer(ConfigLang.SPECTATORTAG.toString(), player));
	}

	public void removePlayer(Player player)
	{
		allPlayers.remove(player);
		allSurvivors.remove(player);
		allSpectators.remove(player);
		if(player == killer)
		{
			killer = null;

			if(task != null)
			{
				Server.getInstance().getPluginManager().callEvent(new GameEndEvent(this, WinType.KILLER_LEAVE));

				for(Player players : allPlayers)
					addSpectator(players);

				cancelTimer();
			}
		}

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

	private interface FinishTask {

		void finish();
	}
}
