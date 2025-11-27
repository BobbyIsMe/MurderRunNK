package com.joshuacc.mrnk.files;

import com.joshuacc.mrnk.main.MRMain;

public class MRTrapsConfig extends AbstractFiles {

	public MRTrapsConfig(MRMain main) 
	{
		super(main, "MRTrapsConfig");
	}

	@Override
	public void addDefaults() 
	{
		addDefault("Item-Cooldown", "&6On cooldown: &c%n seconds left");
	}
	
	public void addTrap(String trap, String path, String image, String name, String description, int price, int material)
	{
		addDefault(trap+".Path", path);
		addDefault(trap+".Image", image);
		addDefault(trap+".Name", name);
		addDefault(trap+".Description", description);
		addDefault(trap+".Price", price);
		addDefault(trap+".Item", material);
	}
	
	public String getItemCooldownText()
	{
		return config.getString("Item-Cooldown");
	}
	
	public String getString(String trap, String key)
	{
		return config.getString(trap+"."+key);
	}
	
	public int getInt(String trap, String key)
	{
		return config.getInt(trap+"."+key);
	}
}
