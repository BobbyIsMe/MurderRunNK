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
import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.listeners.MRGameListener;
import com.joshuacc.mrnk.utils.EmptyGenerator;
import com.joshuacc.mrnk.utils.FormUtils;
import com.joshuacc.mrnk.utils.NPCHuman;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Server;
import cn.nukkit.command.CommandMap;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;

public class MRMain extends PluginBase {

	private MRLobbyConfig lobby;
	private MRScoreboardConfig board;
	private FormUtils formUtil;
	private TextUtils textUtil;
	private HashMap<String,MRArenasConfig> mapConfigs = new HashMap<>();
	private static String prefix;

	@Override
	public void onEnable()
	{
		try {
			Class.forName("de.theamychan.scoreboard.api.ScoreboardAPI");
		} catch (ClassNotFoundException e) {
			getLogger().info("§cMissing dependency: ScoreboardAPI-1.0 by LucGamesHD");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		Generator.addGenerator(EmptyGenerator.class, "emptyworld", Generator.TYPE_INFINITE);
		Entity.registerEntity(NPCHuman.class.getSimpleName(), NPCHuman.class);
		textUtil = new TextUtils();
		MRLanguagesConfig language = new MRLanguagesConfig(this);
		MRFormsTextsConfig forms = new MRFormsTextsConfig(this);
		language.setupConfig();
		prefix = ConfigLang.PREFIXMESSAGE.toString();
		board = new MRScoreboardConfig(this);
		lobby = new MRLobbyConfig(this);
		board.setupConfig();
		forms.setupConfig();
		lobby.setupConfig();
		formUtil = new FormUtils(this);
		registerCommands();
		getServer().getPluginManager().registerEvents(new MRGameListener(this), this);
		MRPlayer.registerListener(this);
		
		newFile("Maps");
		newFile("Normal");
		newFile("Escape");

		if(getMaps() != null)
		{
			for(String maps : getMaps())
			{
				MRArenasConfig config = new MRArenasConfig(this, maps);
				config.setupConfig();
				mapConfigs.put(maps, config);
				if(config.isMapEnabled())
					getLogger().info(maps+" is loading!");
				else
					getLogger().info(maps+" is disabled, not loading!");
				loadNormalModeMaps(maps, config);
				loadEscapeModeMaps(maps, config);
			}
		} else
			getLogger().info("No available maps were found!");
	}
	
	public void loadNormalModeMaps(String maps, MRArenasConfig config)
	{
		if(config.isMapEnabled())
			for(int i = 1; i <= config.getConfig().getInt(maps+".Normal Multiples"); i++)
				new MRTeamNormal(this, maps, config, i);
	}

	public void loadEscapeModeMaps(String maps, MRArenasConfig config)
	{
		if(config.isMapEnabled())
			for(int i = 1; i <= config.getConfig().getInt(maps+".Escape Multiples"); i++)
				new MRTeamEscape(this, maps, config, i);
	}
	
	public void initWorld(String levelName)
	{
		Server.getInstance().generateLevel(levelName, 0, Generator.getGenerator("emptyworld"));
	}
	
	public void removeMapTeam(String maps, int multiple, String type)
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

		if ((files != null) && (files.length > 0))
		{
			String[] str = new String[files.length];
			for(int x = 0; x < str.length; x++)
				str[x] = files[x].getName();
			return str;
		}
		return null;
	}

	public MRLobbyConfig getMRLobbyConfig()
	{
		return lobby;
	}
	
	public MRScoreboardConfig getMRScoreboardConfig()
	{
		return board;
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
}
