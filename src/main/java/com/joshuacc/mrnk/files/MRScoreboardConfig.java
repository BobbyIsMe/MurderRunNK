package com.joshuacc.mrnk.files;

import java.util.ArrayList;
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
		ArrayList<String> queueBoard = new ArrayList<String>();
		queueBoard.add(" &7%m &2GMT+8");
		queueBoard.add(" &8»%m");
		queueBoard.add("");
		queueBoard.add(" Map&6» &e&l%m");
		queueBoard.add(" Players&6» &a%m/%m2");
		queueBoard.add(" Points&6» &a%m");
		queueBoard.add(" ");
		queueBoard.add(" Points Limit&6» &l%lm");
		queueBoard.add(" Queued Pts.&6» &l%m");
		queueBoard.add("  ");
		queueBoard.add("%m");
		queueBoard.add("   ");
		queueBoard.add(" Mode&6» &a&l%m");
		addDefault("Scoreboard-Title", "&0&kIII&r &4&lMurder Run &r&0&kIII");
		addDefault("Scoreboard-Message-1", "&6»&fWaiting for players...");
		addDefault(queue+"Scores", 13);
		addDefault(queue+"Lines", queueBoard);
		addDefault(queue+"ID", 1);
		addDefault(queue+"Map", 3);
		addDefault(queue+"Players", 4);
		addDefault(queue+"Points", 5);
		addDefault(queue+"Limit", 7);
		addDefault(queue+"QPts", 8);
		addDefault(queue+"Message", 10);
		addDefault(queue+"Mode", 12);
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
