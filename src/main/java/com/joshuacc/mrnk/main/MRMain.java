package com.joshuacc.mrnk.main;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import com.joshuacc.mrnk.commands.AreaCommand;
import com.joshuacc.mrnk.commands.MRCommand;
import com.joshuacc.mrnk.commands.OpenListCommand;
import com.joshuacc.mrnk.commands.PlayerCommand;
import com.joshuacc.mrnk.commands.SellCommand;
import com.joshuacc.mrnk.commands.TipCommand;
import com.joshuacc.mrnk.commands.UnqueueCommand;
import com.joshuacc.mrnk.commands.UpCommand;
import com.joshuacc.mrnk.files.MRAreasConfig;
import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.files.MRFormsTextsConfig;
import com.joshuacc.mrnk.files.MRGameConfig;
import com.joshuacc.mrnk.files.MRItemShopConfig;
import com.joshuacc.mrnk.files.MRLanguagesConfig;
import com.joshuacc.mrnk.files.MRLobbyConfig;
import com.joshuacc.mrnk.files.MRPerksConfig;
import com.joshuacc.mrnk.files.MRPlayerConfig;
import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.itemAbility.ItemBuilder;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.listeners.MRGameListener;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.npc.NPCClone;
import com.joshuacc.mrnk.npc.NPCCorpse;
import com.joshuacc.mrnk.npc.NPCLobby;
import com.joshuacc.mrnk.perks.Adrenaline;
import com.joshuacc.mrnk.perks.Claymore;
import com.joshuacc.mrnk.perks.DoubleJump;
import com.joshuacc.mrnk.perks.MysteriousHeadband;
import com.joshuacc.mrnk.scoreboards.PlayScoreboard;
import com.joshuacc.mrnk.traps.BearTrap;
import com.joshuacc.mrnk.traps.Clones;
import com.joshuacc.mrnk.traps.FlyHigh;
import com.joshuacc.mrnk.traps.FullCounter;
import com.joshuacc.mrnk.traps.Nacrotics;
import com.joshuacc.mrnk.traps.Necromancy;
import com.joshuacc.mrnk.traps.Nightmare;
import com.joshuacc.mrnk.traps.PanicSwap;
import com.joshuacc.mrnk.traps.Phase;
import com.joshuacc.mrnk.traps.Pinpoint;
import com.joshuacc.mrnk.traps.Portal;
import com.joshuacc.mrnk.traps.Revive;
import com.joshuacc.mrnk.traps.RollingThunder;
import com.joshuacc.mrnk.traps.ShatteredTime;
import com.joshuacc.mrnk.traps.SpyGlasses;
import com.joshuacc.mrnk.utils.EmptyGenerator;
import com.joshuacc.mrnk.utils.FormUtils;
import com.joshuacc.mrnk.utils.GameScheduler.Schedulers;

import cn.nukkit.Server;
import cn.nukkit.command.CommandMap;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

public class MRMain extends PluginBase {

	private MRLobbyConfig lobby;
	private MRScoreboardConfig board;
	private MRPlayerConfig players;
	private MRItemShopConfig itemShop;
	private MRTrapsConfig traps;
	private MRPerksConfig perks;
	private MRGameConfig game;
	private MRAreasConfig areas;
	private FormUtils formUtil;

	private HashMap<String,MRArenasConfig> mapConfigs = new HashMap<>();
	private HashMap<MapModes,Integer> playerCount = new HashMap<>(); 
	private String empty;

	private String prefix;
	
	private static MRMain instance;

	@Override
	public void onEnable()
	{
		instance = this;
		try {
			Class.forName("de.lucgameshd.scoreboard.api.ScoreboardAPI");
		} catch (ClassNotFoundException e) {
			getLogger().critical("�cMissing dependency: ScoreboardAPI-1.0 by LucGamesHD");
			getLogger().info("");
			getLogger().info("�cDownload Link:");
			getLogger().info("�chttps://github.com/LucGamesYT/ScoreboardAPI/releases/tag/1.0.0");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		//Always not to forget about new instances prior or it will mess me up
		Schedulers.values();
		Generator.addGenerator(EmptyGenerator.class, "emptyworld", Generator.TYPE_INFINITE);
		Entity.registerEntity(NPCLobby.class.getSimpleName(), NPCLobby.class);
		Entity.registerEntity(NPCCorpse.class.getSimpleName(), NPCCorpse.class);
		Entity.registerEntity(NPCClone.class.getSimpleName(), NPCClone.class);

		MRLanguagesConfig language = new MRLanguagesConfig(this);
		MRFormsTextsConfig forms = new MRFormsTextsConfig(this);

		language.setupConfig();
		forms.setupConfig();

		MRTeam.registerMapModes(forms, language);

		prefix = ConfigLang.PREFIXMESSAGE.toString();

		players = new MRPlayerConfig(this);
		itemShop = new MRItemShopConfig(this);
		board = new MRScoreboardConfig(this);
		lobby = new MRLobbyConfig(this);
		game = new MRGameConfig(this);
		traps = new MRTrapsConfig(this);
		perks = new MRPerksConfig(this);
		areas = new MRAreasConfig(this);
		
		this.empty = "@".repeat(board.getMaxLength());

		players.setupConfig();
		itemShop.setupConfig();
		board.setupConfig();
		lobby.setupConfig();
		game.setupConfig();
		areas.setupConfig();
		traps.addDefaults();
		perks.addDefaults();
		
		//Survivor Traps
		MRTraps.addMRTrap(new Nacrotics(), true, this);
		MRTraps.addMRTrap(new FullCounter(), true, this);
		MRTraps.addMRTrap(new Portal(), true, this);
		MRTraps.addMRTrap(new Phase(), true, this);
		MRTraps.addMRTrap(new FlyHigh(), true, this);
		MRTraps.addMRTrap(new PanicSwap(), true, this);
		MRTraps.addMRTrap(new Revive(), true, this);
		MRTraps.addMRTrap(new Clones(), true, this);
		MRTraps.addMRTrap(new ShatteredTime(), true, this);
		
		//Murderer Traps
		MRTraps.addMRTrap(new Nightmare(), false, this);
		MRTraps.addMRTrap(new Pinpoint(), false, this);
		MRTraps.addMRTrap(new SpyGlasses(), false, this);
		MRTraps.addMRTrap(new RollingThunder(), false, this);
		MRTraps.addMRTrap(new BearTrap(), false, this);
		MRTraps.addMRTrap(new Necromancy(), false, this);
		
		//Survivor Perks
		MRPerks.addMRPerk(new Adrenaline(), true, this);
		MRPerks.addMRPerk(new MysteriousHeadband(), true, this);
		
		//Murderer Perks
		MRPerks.addMRPerk(new DoubleJump(), false, this);
		MRPerks.addMRPerk(new Claymore(), false, this);
		
		for(MRTraps survTrap : MRTraps.getTraps(true))
		{
			traps.addTrap(survTrap.getName(), "path", survTrap.getIcon(), survTrap.getTrapName(), survTrap.getTrapDesc(), survTrap.getPrice(), survTrap.getItem(), survTrap.getMeta());
			survTrap.setTrapName();
		}
		
		for(MRTraps murdTrap : MRTraps.getTraps(false))
		{
			traps.addTrap(murdTrap.getName(), "path", murdTrap.getIcon(), murdTrap.getTrapName(), murdTrap.getTrapDesc(), murdTrap.getPrice(), murdTrap.getItem(), murdTrap.getMeta());
			murdTrap.setTrapName();
		}
		
		traps.getConfig().save();
		
		for(MRPerks survPerk : MRPerks.getPerks(true))
		{
			perks.addPerk(survPerk.getName(), "path", survPerk.getIcon(), survPerk.getPerkName(), survPerk.getPerkDesc(), survPerk.getPrice());
			survPerk.setPerkName();
		}
		
		for(MRPerks murdPerk : MRPerks.getPerks(false))
		{
			perks.addPerk(murdPerk.getName(), "path", murdPerk.getIcon(), murdPerk.getPerkName(), murdPerk.getPerkDesc(), murdPerk.getPrice());
			murdPerk.setPerkName();
		}
		
		perks.getConfig().save();
		
		formUtil = new FormUtils(this);

		registerCommands();
		registerListeners();
		
		ItemBuilder.registerItems(lobby);

		newFile("Maps");
		newFile("Normal");
		newFile("Escape");

		for(MapModes mode : MapModes.values())
			playerCount.put(mode, 0);

		PlayScoreboard.registerScoreboard(board);
		
		minigameMapLoader();
	}
	
	public static MRMain getInstance()
	{
		return instance;
	}

	private void minigameMapLoader()
	{
		if(getMaps() != null)
		{
			int enable = 0;
			int disable = 0;
			getLogger().info("�b>>");
			getLogger().info("Loading "+getMaps().length+" maps found, this might take a while...");
			getLogger().info("[===== MINIGAME MAP LOADER =====]");
			getLogger().info("");
			for(String maps : getMaps())
			{
				MRArenasConfig config = new MRArenasConfig(this, maps);
				config.setupConfig();
				mapConfigs.put(maps, config);

				if(config.isMapEnabled() && correctMapAreasConfig(config))
				{
					getLogger().info(TextFormat.GREEN+maps+" is loading!");
					MapModes.loadAllModeMaps(this, maps, config);
					enable++;
				}
				else
				{
					getLogger().warning(TextFormat.RED+maps+" is disabled, not loading!");

					if(!config.isMapEnabled())
						config.loadOriginMap();
					else
						config.toggleMapEnabled();

					if(!correctMapAreasConfig(config))
						checkMapAreas(config);
					disable++;
				}

				getLogger().info("");
			}
			getLogger().info("[===== MINIGAME MAP LOADER =====]");
			getLogger().info("All maps have been loaded.");
			getLogger().info("Enabled Maps Count: "+enable);
			getLogger().info("Disabled Maps Count: "+disable);
			getLogger().info("�b>>");
		} else
			getLogger().warning("No available maps were found!");
	}

	private void newFile(String file)
	{
		File f = new File(getDataFolder().getAbsolutePath(), file);
		if(!f.exists())
			f.mkdir();
	}

	private void registerCommands()
	{
		CommandMap map = getServer().getCommandMap();
		map.register("mr", new MRCommand(this));
		map.register("npcadd", new PlayerCommand(this));
		map.register("openlist", new OpenListCommand(this));
		map.register("unqueue", new UnqueueCommand());
		map.register("tip", new TipCommand());
		map.register("sell", new SellCommand());
		map.register("area", new AreaCommand(this));
		map.register("up", new UpCommand());
	}

	private void registerListeners()
	{
		getServer().getPluginManager().registerEvents(new MRGameListener(this), this);
		getServer().getPluginManager().registerEvents(new ItemBuilder(), this);
		MRPlayer.registerListener(this);
	}

	private void checkMapAreas(MRArenasConfig config)
	{
		getLogger().info("Reason: ");
		if(config.noLocationY("Survivor") && config.noLocationY("Murderer") && config.noLocationY("Game End"))
			getLogger().info("There are no spawns found for the map!");
		else
		{
			if(config.noLocationY("Survivor"))
				getLogger().info("No survivor spawn found!");

			if(config.noLocationY("Murderer"))
				getLogger().info("No murderer spawn found!");

			if(config.noLocationY("Game End"))
				getLogger().info("No game end spawn found!");
		}
	}

	public void updatePlayerCount(MapModes mode, int count)
	{
		playerCount.put(mode, playerCount.get(mode)+count);
	}

	public void removeMapTeam(String maps, int multiple, MapModes type)
	{
		for(int i = 1; i <= multiple; i++)
			if(MRTeam.getMapTeamByID(maps, i, type) != null)
			{
				MRTeam team = MRTeam.getMapTeamByID(maps, i, type);
				if(team.getMapLevel() != null)
					team.getMapLevel().unload();
				team.removeMapTeam();
			}
	}

	public void initWorld(String levelName)
	{
		Server.getInstance().generateLevel(levelName, 0, Generator.getGenerator("emptyworld"));
		Server.getInstance().loadLevel(levelName);
	}

	public Map<String, MRArenasConfig> getMapConfigs()
	{
		return mapConfigs;
	}

	public String getFileDirectory(String path)
	{
		return new File(getDataFolder(), path).getAbsolutePath();
	}
	
	public String getEmpty()
	{
		return empty;
	}

	public String[] getMaps()
	{
		File maps = new File(getDataFolder().getAbsolutePath(), "Maps");
		File[] files = maps.listFiles(new FilenameFilter()
		{
			public boolean accept(File file, String name)
			{
				return file.isDirectory();
			}
		});

		if (files != null && files.length > 0)
		{
			String[] str = new String[files.length];
			for(int x = 0; x < str.length; x++)
				str[x] = files[x].getName();
			return str;
		}
		return null;
	}

	public boolean correctMapAreasConfig(MRArenasConfig config)
	{
		return !config.noLocationY("Survivor") && !config.noLocationY("Murderer") && !config.noLocationY("Game End");
	}

	public int getPlayerCount(MapModes mode)
	{
		return playerCount.get(mode);
	}

	public MRLobbyConfig getMRLobbyConfig()
	{
		return lobby;
	}

	public MRScoreboardConfig getMRScoreboardConfig()
	{
		return board;
	}

	public MRPlayerConfig getMRPlayerConfig()
	{
		return players;
	}
	
	public MRItemShopConfig getMRItemShopConfig()
	{
		return itemShop;
	}

	public MRGameConfig getMRGameConfig()
	{
		return game;
	}
	
	public MRTrapsConfig getMRTrapsConfig()
	{
		return traps;
	}
	
	public MRPerksConfig getMRPerksConfig() 
	{
		return perks;
	}
	
	public MRAreasConfig getMRAreasConfig()
	{
		return areas;
	}

	public FormUtils getFormUtil()
	{
		return formUtil;
	}
	
	public String getPrefix()
	{
		return prefix;
	}

}
