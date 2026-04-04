package com.joshuacc.mrnk.files;

import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.item.Item;

public class MRPerksConfig extends AbstractFiles {

	public MRPerksConfig(MRMain main) 
	{
		super(main, "MRPerksConfig");
	}

	@Override
	public void addDefaults() 
	{
		addDefault("Item", Item.WHITE_WOOL);
		addDefault("Meta", 0);
	}
	
	public void addPerk(String perk, String path, String image, String name, String description, int price)
	{
		addDefault(perk+".Path", path);
		addDefault(perk+".Image", image);
		addDefault(perk+".Name", name);
		addDefault(perk+".Description", description);
		addDefault(perk+".Price", price);
	}
	
	public void putDefault(String perk, String key, Object value)
	{
		addDefault(perk+"."+key, value);
	}
	
	public int getItem()
	{
		return config.getInt("Item");
	}
	
	public int getMeta()
	{
		return config.getInt("Meta");
	}
	
	public String getString(String perk, String key)
	{
		return config.getString(perk+"."+key);
	}
	
	public int getInt(String perk, String key)
	{
		return config.getInt(perk+"."+key);
	}
}
