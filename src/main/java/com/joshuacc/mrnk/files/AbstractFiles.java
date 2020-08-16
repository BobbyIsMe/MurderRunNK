package com.joshuacc.mrnk.files;

import java.io.File;
import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.utils.Config;

public abstract class AbstractFiles {

	protected Config config;
	
	public AbstractFiles(MRMain main, String name)
	{
		this.config = new Config(new File(main.getDataFolder(), name+".yml"), Config.YAML);
	}
	
	public void setupConfig()
	{
		addDefaults();
		config.save();
	}
	
	protected void addDefault(String line, Object value)
	{
		if(!config.exists(line))
		config.set(line, value);
	}
	
	public Config getConfig()
	{
		return config;
	}
	
	public abstract void addDefaults();
}
