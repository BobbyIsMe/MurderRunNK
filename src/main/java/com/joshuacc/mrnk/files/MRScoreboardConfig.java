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
		String play = "Scoreboard-Play.";
		ArrayList<String> queueBoard = new ArrayList<String>();
		ArrayList<String> playBoard = new ArrayList<String>();

		queueBoard.add(" &7%m &2GMT+8");
		queueBoard.add(" &8»%m");
		queueBoard.add("");
		queueBoard.add(" Map&6» &e&l%m");
		queueBoard.add(" Players&6» &a%m/%n");
		queueBoard.add(" Points&6» &a%m");
		queueBoard.add(" ");
		queueBoard.add(" Points Limit&6» &l%m");
		queueBoard.add(" Queued Pts.&6» &l%m");
		queueBoard.add("  ");
		queueBoard.add("%m");
		queueBoard.add("   ");
		queueBoard.add(" Mode&6» &a&l%m");
		queueBoard.add(" Time Limit&6» &a%mm");

		playBoard.add("  &e|| &7[&fKiller&7] &e||");
		playBoard.add(" &6»&c&l%m");
		playBoard.add("");
		playBoard.add(" &7[&fTime&7] &e|| &6» &a%m");
		playBoard.add(" &7[&fMap&7] &e|| &6» &b%m");
		playBoard.add(" ");
		playBoard.add(" &6&l»&r&a%m &esurvivors left...");
		playBoard.add("  ");
		playBoard.add("  &6&lLeaderboards");
		playBoard.add(" &7[&f1&7] &e|| &8%m - %n");
		playBoard.add(" &7[&f2&7] &e|| &8%m - %n");
		playBoard.add(" &7[&f3&7] &e|| &8%m - %n");

		addDefault("Scoreboard-Title", "&0&kIII&r &4&lMurder Run &r&0&kIII");
		addDefault("Scoreboard-Seconds", 15);
		addDefault("Scoreboard-Message-1", "&6»&fWaiting for &bplayers...");
		addDefault("Scoreboard-Message-2", "&6»&fStarting in &b%ns");
		addDefault("Scoreboard-Message-3", "&6»&eGame Intermission...");

		addDefault(queue+"Lines", queueBoard);
		addDefault(queue+"Real Time", 0);
		addDefault(queue+"ID", 1);
		addDefault(queue+"Map", 3);
		addDefault(queue+"Players", 4);
		addDefault(queue+"Points", 5);
		addDefault(queue+"Points Limit", 7);
		addDefault(queue+"QPts", 8);
		addDefault(queue+"Message", 10);
		addDefault(queue+"Mode", 12);
		addDefault(queue+"Time Limit", 13);

		addDefault(play+"Lines", playBoard);
		addDefault(play+"Killer", 1);
		addDefault(play+"Time", 3);
		addDefault(play+"Map", 4);
		addDefault(play+"Players",6);
		addDefault(play+"1", 9);
		addDefault(play+"2", 10);
		addDefault(play+"3", 11);
	}

	public String getString(String s)
	{
		return config.getString("Scoreboard-"+s);
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
