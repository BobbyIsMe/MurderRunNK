package com.joshuacc.mrnk.files;

import com.joshuacc.mrnk.main.MRMain;

public class MRGameConfig extends AbstractFiles {

	public MRGameConfig(MRMain main) 
	{
		super(main, "MRGameConfig");
	}

	@Override
	public void addDefaults() 
	{
		addDefault("Points.Kill", 60);
		addDefault("Points.Round", 80);
		addDefault("Points.Minute", 30);
		addDefault("Points.Survive", 500);
		addDefault("Points.Escape", 300);
		addDefault("Points.Win-1", 600);
		addDefault("Points.Win-2", 400);
		addDefault("Points.Win-3", 200);
		addDefault("Points.Win", 100);
	}

	public int getKillPoints()
	{
		return config.getInt("Points.Kill");
	}

	public int getRoundPoints()
	{
		return config.getInt("Points.Round");
	}

	public int getMinutePoints()
	{
		return config.getInt("Points.Minute");
	}

	public int getSurvivePoints()
	{
		return config.getInt("Points.Survive");
	}

	public int getEscapePoints()
	{
		return config.getInt("Points.Escape");
	}

	public int getWinPoints()
	{
		return config.getInt("Points.Win");
	}

	public int getWinPoints(int position)
	{
		return config.getInt("Points.Win-"+position);
	}
}
