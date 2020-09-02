package com.joshuacc.mrnk.main;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import com.joshuacc.mrnk.commands.MRCommand;
import com.joshuacc.mrnk.commands.OpenListCommand;
import com.joshuacc.mrnk.commands.PlayerCommand;
import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.files.MRFormsTextsConfig;
import com.joshuacc.mrnk.files.MRLanguagesConfig;
import com.joshuacc.mrnk.files.MRLobbyConfig;
import com.joshuacc.mrnk.files.MRPlayerConfig;
import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.listeners.MRGameListener;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.scoreboards.ScoreboardAbstract;
import com.joshuacc.mrnk.utils.EmptyGenerator;
import com.joshuacc.mrnk.utils.FormUtils;
import com.joshuacc.mrnk.utils.NPCHuman;
import com.joshuacc.mrnk.utils.TextUtils;

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
	private FormUtils formUtil;
	private TextUtils textUtil;

	private HashMap<String,MRArenasConfig> mapConfigs = new HashMap<>();
	private HashMap<MapModes,Integer> playerCount = new HashMap<>(); 

	private static String prefix;

	@Override
	public void onEnable()
	{
		try {
			Class.forName("de.theamychan.scoreboard.api.ScoreboardAPI");
		} catch (ClassNotFoundException e) {
			getLogger().critical("�cMissing dependency: ScoreboardAPI-1.0 by LucGamesHD");
			getLogger().info("");
			getLogger().info("�cDownload Link:");
			getLogger().info("�chttps://github.com/LucGamesYT/ScoreboardAPI/releases/tag/1.0.0");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		//Always not to forget about new instances prior or it will mess me up
		Generator.addGenerator(EmptyGenerator.class, "emptyworld", Generator.TYPE_INFINITE);
		Entity.registerEntity(NPCHuman.class.getSimpleName(), NPCHuman.class);

		MRLanguagesConfig language = new MRLanguagesConfig(this);
		MRFormsTextsConfig forms = new MRFormsTextsConfig(this);

		language.setupConfig();
		forms.setupConfig();

		MRTeam.registerMapModes(forms, language);

		prefix = ConfigLang.PREFIXMESSAGE.toString();

		textUtil = new TextUtils();
		players = new MRPlayerConfig(this);
		board = new MRScoreboardConfig(this);
		lobby = new MRLobbyConfig(this);
		formUtil = new FormUtils(this);

		players.setupConfig();
		board.setupConfig();
		lobby.setupConfig();

		ScoreboardAbstract.registerScoreboards(board);

		registerCommands();
		registerListeners();

		newFile("Maps");
		newFile("Normal");
		newFile("Escape");

		for(MapModes mode : MapModes.values())
			playerCount.put(mode, 0);

		minigameMapLoader();
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
	}

	private void registerListeners()
	{
		getServer().getPluginManager().registerEvents(new MRGameListener(this), this);
		MRPlayer.registerListener(this);
	}

	private void checkMapAreas(MRArenasConfig config)
	{
		getLogger().info("Reason: ");
		if(config.noLocationY("Survivor"))
			getLogger().info("No survivor spawn found!");

		if(config.noLocationY("Murderer"))
			getLogger().info("No murderer spawn found!");

		if(config.noLocationY("Game End"))
			getLogger().info("No game end spawn found!");
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
	}

	public Map<String, MRArenasConfig> getMapConfigs()
	{
		return mapConfigs;
	}

	public String getFileDirectory(String path)
	{
		return new File(getDataFolder(), path).getAbsolutePath();
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

	public FormUtils getFormUtil()
	{
		return formUtil;
	}

	public TextUtils getTextUtil()
	{
		return textUtil;
	}

	public static String getPrefix()
	{
		return prefix;
	}
}
