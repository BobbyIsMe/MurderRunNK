package com.joshuacc.mrnk.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.events.GameEndEvent.WinType;
import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.files.MRFormsTextsConfig;
import com.joshuacc.mrnk.files.MRLanguagesConfig;
import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.scoreboards.PlayScoreboard;
import com.joshuacc.mrnk.utils.BackupWorlds;
import com.joshuacc.mrnk.utils.MapState;
import com.joshuacc.mrnk.utils.TaskDelay;
import com.joshuacc.mrnk.utils.TaskQueue;
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

	private final String levelName;
	private final String map;
	private final String mapId;
	private final String directory;
	private String message;

	private ArrayList<Player> allPlayers;
	private ArrayList<Player> allSurvivors;
	private ArrayList<Player> allSpectators;
	private ArrayList<Player> rankings;

	private Player killer;
	private MRArenasConfig mapConfig;
	private MRScoreboardConfig board;
	private PlayScoreboard playBoard;

	private boolean started;
	private boolean interm;
	
	private int round = 0;
	
	public MRTeam(MRMain main, String map, MapModes mode, MRArenasConfig mapConfig, int multiple)
	{
		this.main = main;
		this.levelName =  new File(main.getFileDirectory("Maps"), map)+File.separator;
		this.map = map;
		this.mapId = map+"-"+multiple;
		this.mode = mode;
		this.mapConfig = mapConfig;
		this.board = main.getMRScoreboardConfig();
		this.message = TextFormat.colorize('&', board.getString("Message-1"));
		this.directory = new File(main.getFileDirectory(getMode()), mapId)+File.separator;
		
		mapMode.get(mode).put(mapId, this);
		
		initialize();

		new BackupWorlds(main, getMode()).copyOverWorld(map, mapId);
		main.initWorld(directory);
	}
	
	private void initialize()
	{
		killer = null;
		task = null;
		playBoard = null;
		started = false;
		interm = false;
		this.round = 1;
		allPlayers = new ArrayList<>();
		allSurvivors = new ArrayList<>();
		allSpectators = new ArrayList<>();
		rankings = new ArrayList<>();
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
		if(task != null)
		{
			task.cancel();
			task = null;
		}

		this.playBoard = null;
		if(allPlayers.size() == 0)
		{
			initialize();
			return;	
		}
		
		Server.getInstance().getScheduler().scheduleDelayedTask(main, () -> {

			allSpectators.clear();

			for(Player player : allPlayers)
			{
				addSurvivor(player);
				MRPlayer.getMRPlayer(player).queue(true);
				player.setGamemode(0);
				player.setHealth(player.getMaxHealth());
				player.getFoodData().sendFoodLevel(player.getFoodData().getMaxLevel());
			}

			//updateEntry("Message", board.getString("Message-3"));
			round++;
			message = TextFormat.colorize('&', board.getString("Message-3"));
			sendActionBar();
			updateScoreboardPlayerCount();
			selectMurderer();
		}, 40);
	}

	public void startQueueLobby()
	{
		Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() {

			int i = board.getInt("Seconds");

			@Override
			public void onRun(int arg0) 
			{
				if(allPlayers.size() >= mapConfig.getMinimumPlayers())
				{
//					updateEntry("Message", board.getString("Message-2"), i+"");
					message = TextFormat.colorize('&', board.getString("Message-2").replace("%n", i+""));
					sendActionBar();
					if(i == 0)
					{
						officialStart();
						this.cancel();
					}
				}
				else
				{
//					updateEntry("Message", board.getString("Message-1"));
					message = TextFormat.colorize('&', board.getString("Message-1"));
					sendActionBar();
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

//		updateEntry("Message", board.getString("Message-3"));
		message = TextFormat.colorize('&', board.getString("Message-3"));
		sendActionBar();
		TaskDelay task = new TaskDelay(main);
		task.addTask(new TaskQueue(1) {

			@Override
			public void doTask() 
			{
				playSoundMessage(TextUtils.format(ConfigLang.MAPSTARTED.toString()), Sound.MOB_VILLAGER_IDLE);
			}
		});
		task.addTask(new TaskQueue(1) {

			@Override
			public void doTask() 
			{
				for(Player players : allPlayers)
				{
					playSoundPlayer(players, Sound.AMBIENT_WEATHER_LIGHTNING_IMPACT);
					playSoundPlayer(players, Sound.MOB_WITHER_SPAWN);
					players.sendTitle(ConfigLang.MAPTITLESTART.toString(), ConfigLang.MAPSUBTSTART.toString(), 20, 40, 20);

					players.removeEffect(Effect.BLINDNESS);
					players.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(20).setVisible(false));
				}
			}
		});
		task.addTask(new TaskQueue(3) {

			@Override
			public void doTask() 
			{
				playSoundMessage(TextUtils.format(ConfigLang.SELECTKILLER.toString()), Sound.NOTE_BASS);
				selectMurderer();
			}
		});
		task.startTasks();
	}

	private void selectMurderer()
	{
		if(!enoughPlayers())
			return;
		
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
			main.getServer().getScheduler().scheduleDelayedTask(main, () -> intermission(randomPlayers.get(0)), 20);
		}

		else if(size == 0)
		{
			int delay = 2;
			TaskDelay task = new TaskDelay(main);
			playSoundMessage(TextUtils.format(ConfigLang.WINNERDRUMROLL.toString()), Sound.MOB_VILLAGER_IDLE);
			if(rankings.size() > 0 && rankings.get(0) != null)
			{
				delay = 4;
				Player winner = rankings.get(0);
				task.addTask(new TaskQueue(2) {

					@Override
					public void doTask() 
					{
						playTitleAll(TextUtils.formatPlayer(ConfigLang.WINNERANNOUNCENAME.toString(), winner), ConfigLang.WINNERANNOUNCETIME.toString().replace("%n", TextUtils.getTimeFormat(MRPlayer.getMRPlayer(winner).getPlayerTime())), Sound.RANDOM_LEVELUP, 20);
					}
					
				});
				task.addTask(new TaskQueue(2) {

					@Override
					public void doTask() {
						playSoundMessage(TextUtils.format(ConfigLang.LEADERBOARDTITLE.toString()), Sound.RANDOM_CLICK);
						
						for(int i = 0; i < rankings.size(); i++)
						{
							Player players = rankings.get(i);
							messageAllPlayers(TextUtils.format(TextUtils.formatPlayer(ConfigLang.LEADERBOARDRANK.toString().replace("%n", Integer.toString(i+1)).replace("%m", TextUtils.getTimeFormat(MRPlayer.getMRPlayer(players).getPlayerTime())), players)));
						}
					}
					
				});
			}
			else
			{
				task.addTask(new TaskQueue(1) {

					@Override
					public void doTask() 
					{
						playTitleAll(TextFormat.colorize('&', ConfigLang.WINNERANNOUNCENONE.toString()), "", Sound.RANDOM_LEVELUP, 20);
					}
					
				});
			}
			task.addTask(new TaskQueue(delay) {

				@Override
				public void doTask() 
				{
					for(Player players : allPlayers)
					{
						MRPlayer.getMRPlayer(players).unqueue();
					}
					main.updatePlayerCount(mode, -allPlayers.size());
					initialize();
				}
				
			});
			task.startTasks();
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
		intermissionCount(40, mapConfig.getPreparingTime(), () -> releaseMurderer(), allPlayers, ConfigLang.INTERMISSION.toString(), ConfigLang.INTERCOUNT.toString());
	}

	private void releaseMurderer()
	{
		interm = false;
		this.playBoard = new PlayScoreboard(this, main);
		playBoard.openScoreboard();

		for(Player player: allPlayers)
		{
			MRPlayer.getMRPlayer(player).setScoreboard(null);
			player.closeFormWindows();
		}

		removeActionBar();
		sendScoreboardTip();

		for (Player survivor : allSurvivors) {
			survivor.teleport(mapConfig.getSurvivorLocation(getMapLevel()));
		}

		Server.getInstance().getPluginManager().callEvent(new GameStartEvent(GameAttribute.STARTED, this));
		killer.sendMessage(TextUtils.format(TextUtils.formatNumber(ConfigLang.MURDCOUNT.toString(), mapConfig.getHidingTime())));
		playSoundPlayer(killer, Sound.NOTE_PLING);

		intermissionCount(20, mapConfig.getHidingTime(), () -> {

			killer.teleport(mapConfig.getMurdererLocation(getMapLevel()));
			killer.sendMessage(TextUtils.format(ConfigLang.RELEASEMURD.toString()));
			main.getServer().getScheduler().scheduleDelayedTask(main, () -> killer.getLevel().addSound(killer, Sound.BLOCK_END_PORTAL_SPAWN), 5);
			startMurdererTimer();

		}, allSurvivors, ConfigLang.MURDERANNOUNCE.toString(), ConfigLang.MURDCOUNT.toString());
	}

	private void startMurdererTimer()
	{
		for(Player players : allPlayers)
			main.getMRPlayerConfig().incrementPoints(players, MRPlayer.getMRPlayer(players).getPlayerQueuedPoints() - mapConfig.getPointsLimit());

		task = Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(new Task() {

			MRPlayer k = MRPlayer.getMRPlayer(killer);
			int time = mapConfig.getTimeLimit();
			int y = mapConfig.getYLevelStart();

			@Override
			public void onRun(int arg0) 
			{
				if(task == null || killer == null)
					return;

				k.updateTime();

				int i = k.getPlayerTime();
				
				for(Player surv : allSurvivors)
				{
					if(surv.getY() >= y)
						surv.attack(2);
				}

				updateEntry(playBoard.getInt("Timer"), TextUtils.formatLine(playBoard.getString("Timer-Line"), TextUtils.getTimeFormat(i)));
				
				if(i % mapConfig.getYLevelTime() == 0)
				{
					y-= mapConfig.getYLevelDecrement();
					for(Player all : allPlayers)
					{
						all.sendMessage(TextUtils.format(ConfigLang.YLEVELANNOUNCE.toString().replace("%n", Integer.toString(y))));
					}
					updateEntry(playBoard.getInt("Y Level Limit"), TextUtils.formatLine(playBoard.getString("Y Level Limit-Line"), Integer.toString(y)));
				}
				
				sendScoreboardTip();

				if(i == time)
				{	
					Server.getInstance().getPluginManager().callEvent(new GameEndEvent(k.getMapTeam(), WinType.OUT_OF_TIME));
					this.cancel();
					return;
				}
			}

		}, 20, 20);
	}

	private void intermissionCount(int delay, int seconds, FinishTask task, ArrayList<Player> player, String announce, String countdown)
	{
		main.getServer().getScheduler().scheduleDelayedRepeatingTask(new Task() {

			int i = seconds;
			int time = seconds;

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

				if(!enoughPlayers())
				{
					this.cancel();
					return;
				}

				if(i == time)
				{
					if(playBoard == null)
						interm = true;
					playSoundMessage(player, TextUtils.format(TextUtils.formatNumber(announce, i)), Sound.RANDOM_ANVIL_USE);
				}

				else if(i % 60 == 0 || i == 15 || i == 10 || i <= 5)
					playSoundMessage(TextUtils.format(TextUtils.formatNumber(countdown, i)), Sound.RANDOM_CLICK);

				i--;
			}

		}, delay, 20);
	}
	
	private boolean enoughPlayers()
	{
		if(allPlayers.size() <= 0) //TODO: Remember to change the value to 1
		{
			allSurvivors.clear();
			
			playBoard = null;
			round = 1;
			for(Player players : allPlayers)
			{
				MRPlayer.getMRPlayer(players).setHasRound(false);
				players.sendMessage(ConfigLang.NOTENOUGHPLAYERS.toString());
				playSoundPlayer(players, Sound.MOB_VILLAGER_NO);
				players.setNameTag(TextUtils.formatPlayerMap(ConfigLang.QUEUETAG.toString(), players, map));
				players.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(Integer.MAX_VALUE).setVisible(false));
				message = TextFormat.colorize('&', board.getString("Message-1"));
				sendActionBar();
				
				if(playBoard != null)
				MRPlayer.getMRPlayer(players).queue(true);
			}
			
			updateScoreboardPlayerCount();
			if(started)
				started = false;
			
			return false;
		}
		return true;
	}
	
	public void addPlayerRankingByTime(Player player) 
	{
	    Comparator<Player> timeComparator = Comparator
	        .comparingInt((Player p) -> MRPlayer.getMRPlayer(p).getPlayerTime())
	        .thenComparing(Player::getName);
	    
	    int index = Collections.binarySearch(rankings, player, timeComparator);
	    int insertionIndex = index < 0 ? -index - 1 : index;
	    
	    rankings.add(insertionIndex, player);
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

	public void playTitleDelay(String title, String sub, Sound sound, int delay)
	{
		main.getServer().getScheduler().scheduleDelayedTask(main, () -> playTitleAll(title, sub, sound, 20), delay * 20);
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

	public void playTitleAll(String title, String sub, Sound sound, int i)
	{
		playTitleAll(allPlayers, title, sub, sound, 20, i);
	}

	public void playTitleAll(ArrayList<Player> players, String title, String sub, Sound sound, int i, int l)
	{
		for(Player player : players)
		{
			player.sendTitle(title, sub, i, l, i);
			playSoundPlayer(player, sound);
		}
	}
	
	public void updateScoreboardPlayerCount()
	{
//		updateEntry("Players", getPlayers().size()+"", mapConfig.getMaximumPlayers()+"");
		String na = board.getString("NA-Translation");
		int size = rankings.size();
		Player rank1 = size > 0 ? rankings.get(0) : null;
		Player rank2 = size > 1 ? rankings.get(1) : null;
		Player rank3 = size > 2 ? rankings.get(2) : null;
		updateEntry(board.getInt("Queue.Players"), TextUtils.formatLine(board.getString("Queue.Players-Line"), Integer.toString(getPlayers().size()), Integer.toString(mapConfig.getMaximumPlayers())));
		updateEntry(board.getInt("Queue.Round"), TextUtils.formatLine(board.getString("Queue.Round-Line"), Integer.toString(getRound()), Integer.toString(getPlayers().size())));
		updateEntry(board.getInt("Queue.Rank-1"), rank1 != null ? TextUtils.formatLine(board.getString("Queue.Rank-Line"), rank1.getName(), TextUtils.getTimeFormat(MRPlayer.getMRPlayer(rank1).getPlayerTime())) : na);
		updateEntry(board.getInt("Queue.Rank-2"), rank2 != null ? TextUtils.formatLine(board.getString("Queue.Rank-Line"), rank2.getName(), TextUtils.getTimeFormat(MRPlayer.getMRPlayer(rank2).getPlayerTime())) : na);
		updateEntry(board.getInt("Queue.Rank-3"), rank3 != null ? TextUtils.formatLine(board.getString("Queue.Rank-Line"), rank3.getName(), TextUtils.getTimeFormat(MRPlayer.getMRPlayer(rank3).getPlayerTime())) : na);
		sendScoreboardTip();
	}

	public void updateEntry(int index, String p)
	{
		if(playBoard == null)
		for(Player players : allPlayers)
		{
			MRPlayer.getMRPlayer(players).getScoreboard().updateEntry(index, p);
		} else {
			playBoard.updateEntry(index, p);
		}
	}
	
	public void updateEntry(int index, int p)
	{
		updateEntry(index, Integer.toString(p));
	}

	public void sendActionBar()
	{
		if(message.length() != 0)
		for(Player players : allPlayers)
			players.sendActionBar(message);
	}
	
	public void removeActionBar()
	{
		for(Player players : allPlayers)
			players.sendActionBar("!bar");
	}
	
	public void sendScoreboardTip() 
	{
		if(playBoard == null)
		for(Player players : allPlayers)
		{
			MRPlayer.getMRPlayer(players).getScoreboard().sendScoreboardTip(players, "!stop2");
		} else
			playBoard.sendScoreboardTip(allPlayers, "!stop1");
	}

	public Level getMapLevel()
	{
		return Server.getInstance().getLevelByName(directory);
	}

	public String getMapOrigin()
	{
		return map;
	}
	
	public String getMapLevelOriginName()
	{
		return levelName;
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
	
	public int getRound()
	{
		return round;
	}
	
	public PlayScoreboard getPlayBoard()
	{
		return playBoard;
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
		MRPlayer.getMRPlayer(player).setHasRound(true);
	}

	public void addSpectator(Player player)
	{
		player.setGamemode(3);
		allSpectators.add(player);
		player.setNameTag(TextUtils.formatPlayer(ConfigLang.SPECTATORTAG.toString(), player));
	}
	
	public void removeSurvivor(Player player)
	{
		allSurvivors.remove(player);
	}
	
	public void removeAllSurvivors()
	{
		allSurvivors.clear();
	}

	public void removePlayer(Player player)
	{
		allPlayers.remove(player);
		allSurvivors.remove(player);
		allSpectators.remove(player);
		rankings.remove(player);
		if(player == killer)
		{
			killer = null;

			if(task != null)
			{
				for(Player players : allPlayers)
				{
					player.sendMessage(TextUtils.format(ConfigLang.KILLERLEAVE.toString()));
					addSpectator(players);
				}

				Server.getInstance().getPluginManager().callEvent(new GameEndEvent(this, WinType.KILLER_LEAVE));
			}
		}

		main.updatePlayerCount(mode, -1);
	}

	public boolean onIntermission()
	{
		return interm;
	}

	public boolean timerGoing()
	{
		return task != null;
	}
	
	public ArrayList<Player> getPlayers()
	{
		return allPlayers;
	}

	public ArrayList<Player> getSurvivors() {
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