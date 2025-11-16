package com.joshuacc.mrnk.files;

import java.util.List;

import com.joshuacc.mrnk.main.MRMain;

public class MRScoreboardConfig extends AbstractFiles {

	public MRScoreboardConfig(MRMain main) 
	{
		super(main, "MRScoreboardConfig");
	}

	@Override
	public void addDefaults() 
	{
		String queue = "Scoreboard-Queue.";
		String play = "Scoreboard-Play.";

		addDefault("Max Length", 24);
		addDefault("Scoreboard-Time-Translation", "minutes");
		addDefault("Scoreboard-Title", "&0&kIII&r &4&lMurder Run &r&0&kIII");
		addDefault("Scoreboard-Seconds", 15);
		addDefault("Scoreboard-Message-1", "&6»&fWaiting for &bplayers...");
		addDefault("Scoreboard-Message-2", "&6»&fStarting in &b%ns");
		addDefault("Scoreboard-Message-3", "&6»&eGame Intermission...");

		addDefault(queue+"Map", 0);
		addDefault(queue+"Mode", 1);
		addDefault(queue+"Players", 2);
		addDefault(queue+"Time Limit", 3);
		addDefault(queue+"Round", 4);
		addDefault(queue+"Points", 5);

		addDefault(play+"Map", 0);
		addDefault(play+"Timer", 1);
		addDefault(play+"Mode", 2);
		addDefault(play+"Killer", 3);
		addDefault(play+"Survivors Left", 4);
	}

	
	public String getString(String s)
	{
		StringBuilder str = new StringBuilder("Scoreboard-");
		str.append(s);
		return config.getString(str.toString());
	}
	
	public int getInt(String s)
	{
		StringBuilder str = new StringBuilder("Scoreboard-");
		str.append(s);
		return config.getInt(str.toString());
	}
	
	public int getMaxLength()
	{
		return config.getInt("Max Length");
	}
	
	public String getTip(String prefix, String value) 
	{
		StringBuilder s = new StringBuilder(prefix);
		s.append("@").append(value);
		int padding = getMaxLength() - s.length();
		
		return padding > 0 ? s.append("@".repeat(padding)).toString() : s.substring(0, getMaxLength());
	}
	
	public String getTip(String prefix, int value)
	{
		return getTip(prefix, Integer.toString(value));
	}

	public String getScoreboardTitle()
	{
		return config.getString("Scoreboard-Title");
	}

	public List<String> getQueueBoardLines()
	{
		return config.getStringList("Scoreboard-Queue.Lines");
	}
}
