package com.joshuacc.mrnk.files;

import java.io.File;

import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;

public class MRArenasConfig extends AbstractFiles {

	private String mapName;
	private String levelName;
	private Level level;
	private MRMain main;

	public MRArenasConfig(MRMain main, String mapName) {
		super(main, "MRArenasConfig");
		this.mapName = mapName;
		this.main = main;
		this.levelName = new File(main.getFileDirectory("Maps"), mapName)+File.separator;
		this.level = null;
		if(!isMapEnabled())
		{
			main.initWorld(levelName);
			level = Server.getInstance().getLevelByName(levelName);
		}
	}

	@Override
	public void addDefaults() 
	{
		String prefix = mapName+".";
		String mLocation = "Murderer Location.";
		String sLocation = "Survivor Location.";
		String eLocation = "Game End Location.";
		addDefault(prefix+"Enabled", true);
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
			main.removeMapTeam(mapName, getNormalMultiples(), "Normal");
			main.removeMapTeam(mapName, getEscapeMultiples(), "Escape");
			main.initWorld(levelName);
			level = Server.getInstance().getLevelByName(levelName);
		} else {
			config.set(mapName+".Enabled", true);
			main.loadNormalModeMaps(mapName, this);
			main.loadEscapeModeMaps(mapName, this);
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

	public void setSurvivorLocation()
	{
		String prefix = mapName+".Survivor Location.";
		config.set(prefix+"X", 0);
		config.set(prefix+"Y", 0);
		config.set(prefix+"Z", 0);
		config.set(prefix+"Yaw", 0);
		config.set(prefix+"Pitch", 0);
		config.save();
	}

	public void setMurdererLocation()
	{
		String prefix = mapName+".Game End Location.";
		config.set(prefix+"X", 0);
		config.set(prefix+"Y", 0);
		config.set(prefix+"Z", 0);
		config.set(prefix+"Yaw", 0);
		config.set(prefix+"Pitch", 0);
		config.save();
	}

	public void setGameEndLocation()
	{
		String prefix = mapName+".Vehicle Location.";
		config.set(prefix+"X", 0);
		config.set(prefix+"Y", 0);
		config.set(prefix+"Z", 0);
		config.set(prefix+"Yaw", 0);
		config.set(prefix+"Pitch", 0);
		config.save();
	}

	public void addVehicleLocation()
	{
		String prefix = mapName+".Vehicle Location.";
		config.set(prefix+"X", 0);
		config.set(prefix+"Y", 0);
		config.set(prefix+"Z", 0);
		config.set(prefix+"Yaw", 0);
		config.set(prefix+"Pitch", 0);
		config.save();
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

	public String getInt(String val)
	{
		return String.valueOf(config.getInt(mapName+"."+val+" Multiples"));
	}
	
	public int getNormalMultiples()
	{
		return config.getInt(mapName+".Normal Multiples");
	}
	
	public int getEscapeMultiples()
	{
		return config.getInt(mapName+".Escape Multiples");
	}

	public boolean isMapEnabled()
	{
		return config.getBoolean(mapName+".Enabled") == true;
	}
}
