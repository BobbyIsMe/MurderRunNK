package com.joshuacc.mrnk.files;

import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.Player;

public class MRPlayerConfig extends AbstractFiles {

	public MRPlayerConfig(MRMain main) {
		super(main, "MRPlayerConfig");
	}

	@Override
	public void addDefaults() 
	{
		addDefault("Default Points", 700);
	}

	public void addPlayerData(Player player)
	{
		String name = player.getName()+".";
		if(config.getInt(name+"Level") == 0)
		{
			config.set(name+"Level", 1);
			config.set(name+"XP", 0);
			config.set(name+"Wins", 0);
			config.set(name+"Losses", 0);
			config.set(name+"Points", config.get("Default Points"));
			config.set(name+"Traps Activated", 0);
			config.set(name+"Traps Triggered", 0);
			config.set(name+"Recent Time", 0);
			config.set(name+"Best Time", 0);
			config.save();
		}
	}

	public int getPoints(Player player)
	{
		return getInt(player, "Points");
	}

	public int getInt(Player player, String value)
	{
		return config.getInt(player.getName()+"."+value);
	}
}
