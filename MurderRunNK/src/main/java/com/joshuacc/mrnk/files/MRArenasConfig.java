package com.joshuacc.mrnk.files;

import java.io.File;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam.MapModes;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;

public class MRArenasConfig extends AbstractFiles {

	private String mapName;
	private String levelName;

	private MRMain main;
	private Level level;

	public MRArenasConfig(MRMain main, String mapName) {
		super(main, "MRArenasConfig");
		this.mapName = mapName;
		this.main = main;
		this.levelName = new File(main.getFileDirectory("Maps"), mapName)+File.separator;
		this.level = null;
	}

	@Override
	public void addDefaults() 
	{
		String prefix = mapName+".";
		String mLocation = "Murderer Location.";
		String sLocation = "Survivor Location.";
		String eLocation = "Game End Location.";
		addDefault(prefix+"Enabled", false);
		addDefault(prefix+"Preparing Time", 30);
		addDefault(prefix+"Time Limit", 480);
		addDefault(prefix+"Points Limit", 500);
		addDefault(prefix+"Minimum Players", 3);
		addDefault(prefix+"Maximum Players", 4);
		addDefault(prefix+"Normal Multiples", 1);
		addDefault(prefix+"Escape Multiples", 0);

		addDefault(prefix+mLocation+"X", 0);
		addDefault(prefix+mLocation+"Y", 0);
		addDefault(prefix+mLocation+"Z", 0);
		addDefault(prefix+mLocation+"Yaw", 0);
		addDefault(prefix+mLocation+"Pitch", 0);

		addDefault(prefix+sLocation+"X", 0);
		addDefault(prefix+sLocation+"Y", 0);
		addDefault(prefix+sLocation+"Z", 0);
		addDefault(prefix+sLocation+"Yaw", 0);
		addDefault(prefix+sLocation+"Pitch", 0);

		addDefault(prefix+eLocation+"X", 0);
		addDefault(prefix+eLocation+"Y", 0);
		addDefault(prefix+eLocation+"Z", 0);
		addDefault(prefix+eLocation+"Yaw", 0);
		addDefault(prefix+eLocation+"Pitch", 0);
	}

	public void setValue(String key, String value)
	{
		config.set(mapName+"."+key, Integer.parseInt(value));
	}

	public void toggleMapEnabled()
	{	
		if(isMapEnabled())
		{
			config.set(mapName+".Enabled", false);

			for(MapModes modes : MapModes.values())
				main.removeMapTeam(mapName, getMultiples(modes), modes);

			loadOriginMap();
		} else {
			config.set(mapName+".Enabled", true);
			MapModes.loadAllModeMaps(main, mapName, this);

			if(level.getPlayers().values().size() != 0)
				main.getLogger().warning("§eThere were players in arena "+mapName+" and it got enabled, they are now sent back to lobby level.");

			for(Player players : level.getPlayers().values())
			{
				players.sendMessage("Level unloaded, you were teleported back to lobby!");
				players.teleport(main.getMRLobbyConfig().getMainLobbyLocation());
			}

			if(level != null)
				level.unload(true);

			level = null;
		}
		config.save();
	}

	public boolean noLocationY(String loc)
	{
		return config.getInt(mapName+"."+loc+" Location.Y") == 0;
	}

	public void loadOriginMap()
	{
		if(level == null)
		{
			main.initWorld(levelName);
			level = Server.getInstance().getLevelByName(levelName);
		}
	}

	public void setSurvivorLocation(Player player)
	{
		String prefix = mapName+".Survivor Location.";
		config.set(prefix+"X", player.x);
		config.set(prefix+"Y", player.y);
		config.set(prefix+"Z", player.z);
		config.set(prefix+"Yaw", player.yaw);
		config.set(prefix+"Pitch", player.pitch);
		config.save();
	}

	public void setMurdererLocation(Player player)
	{
		String prefix = mapName+".Murderer Location.";
		config.set(prefix+"X", player.x);
		config.set(prefix+"Y", player.y);
		config.set(prefix+"Z", player.z);
		config.set(prefix+"Yaw", player.yaw);
		config.set(prefix+"Pitch", player.pitch);
		config.save();
	}

	public void setGameEndLocation(Player player)
	{
		String prefix = mapName+".Game End Location.";
		config.set(prefix+"X", player.x);
		config.set(prefix+"Y", player.y);
		config.set(prefix+"Z", player.z);
		config.set(prefix+"Yaw", player.yaw);
		config.set(prefix+"Pitch", player.pitch);
		config.save();
	}

	public void addVehicleLocation(Player player)
	{
		String prefix = mapName+".Vehicle Location.";
		config.set(prefix+"X", player.x);
		config.set(prefix+"Y", player.y);
		config.set(prefix+"Z", player.z);
		config.set(prefix+"Yaw", player.yaw);
		config.set(prefix+"Pitch", player.pitch);
		config.save();
	}

	public Location getSurvivorLocation(Level level)
	{
		String prefix = mapName+".Survivor Location.";
		return new Location(
				config.getDouble(prefix+"X"), 
				config.getDouble(prefix+"Y"),
				config.getDouble(prefix+"Z"),
				config.getDouble(prefix+"Yaw"),
				config.getDouble(prefix+"Pitch"), level);
	}

	public Location getMurdererLocation(Level level)
	{
		String prefix = mapName+".Murderer Location.";
		return new Location(
				config.getDouble(prefix+"X"), 
				config.getDouble(prefix+"Y"),
				config.getDouble(prefix+"Z"),
				config.getDouble(prefix+"Yaw"),
				config.getDouble(prefix+"Pitch"), level);
	}

	public Location getGameEndLocation(Level level)
	{
		String prefix = mapName+".Game End Location.";
		return new Location(
				config.getDouble(prefix+"X"), 
				config.getDouble(prefix+"Y"),
				config.getDouble(prefix+"Z"),
				config.getDouble(prefix+"Yaw"),
				config.getDouble(prefix+"Pitch"), level);
	}

	public Location getVehicleLocation(Level level)
	{
		String prefix = mapName+".Vehicle Location.";
		return new Location(
				config.getDouble(prefix+"X"), 
				config.getDouble(prefix+"Y"),
				config.getDouble(prefix+"Z"),
				config.getDouble(prefix+"Yaw"),
				config.getDouble(prefix+"Pitch"), level);
	}

	public int getMinimumPlayers()
	{
		return config.getInt(mapName+".Minimum Players");
	}

	public int getMaximumPlayers()
	{
		return config.getInt(mapName+".Maximum Players");
	}

	public int getPreparingTime()
	{
		return config.getInt(mapName+".Preparing Time");
	}

	public int getPointsLimit()
	{
		return config.getInt(mapName+".Points Limit");
	}

	public int getTimeLimit()
	{
		return config.getInt(mapName+".Time Limit");
	}

	public Level getOriginalMapLevel()
	{
		return level;
	}

	public String getImageURL()
	{
		return config.getString(mapName+".Image URL");
	}

	public String getInt(String val)
	{
		return String.valueOf(config.getInt(mapName+"."+val));
	}

	public int getMultiples(MapModes mode)
	{
		return config.getInt(mapName+"."+mode.getMode()+" Multiples");
	}

	public boolean isMapEnabled()
	{
		return config.getBoolean(mapName+".Enabled");
	}
}
