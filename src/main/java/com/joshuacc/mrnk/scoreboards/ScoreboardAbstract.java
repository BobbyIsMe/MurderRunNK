package com.joshuacc.mrnk.scoreboards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplayEntry;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import de.theamychan.scoreboard.network.SortOrder;

public abstract class ScoreboardAbstract {

	protected static ArrayList<Integer> queueInt = new ArrayList<>();

	protected Player player;

	private Scoreboard board;
	private ScoreboardDisplay display;

	protected HashMap<Integer,DisplayEntry> entry;

	private MRScoreboardConfig config;
	private ArrayList<Integer> score;
	private String key;
	private int line;

	public ScoreboardAbstract(Player player, String objName, String key, ArrayList<Integer> score, MRMain main)
	{
		this.entry = new HashMap<>();
		this.player = player;
		this.key = key+".";
		this.score = score;
		this.board = ScoreboardAPI.createScoreboard();
		this.config = main.getMRScoreboardConfig();
		this.display = board.addDisplay(DisplaySlot.SIDEBAR, objName, TextFormat.colorize(config.getScoreboardTitle()), SortOrder.DESCENDING);
		this.line = getStringList().size()-1;
	}

	protected abstract void scoreboardStuff();

	public static void registerScoreboards(MRScoreboardConfig config)
	{
		String queue = "Scoreboard-Queue.";
		Config c = config.getConfig();
		queueInt.add(c.getInt(queue+"Real Time"));
		queueInt.add(c.getInt(queue+"ID"));
		queueInt.add(c.getInt(queue+"Map"));
		queueInt.add(c.getInt(queue+"Players"));
		queueInt.add(c.getInt(queue+"Points"));
		queueInt.add(c.getInt(queue+"Points Limit"));
		queueInt.add(c.getInt(queue+"QPts"));
		queueInt.add(c.getInt(queue+"Message"));
		queueInt.add(c.getInt(queue+"Mode"));
		queueInt.add(c.getInt(queue+"Time Limit"));
	}

	public void openScoreboard()
	{
		for(int i = 0; i < getStringList().size(); i++)
			if(!score.contains(i))
				addLine(i);
			else
				entry.put(i, addLine(i));

		scoreboardStuff();
		ScoreboardAPI.setScoreboard(player, board);
	}

	public void removeScoreboard()
	{
		ScoreboardAPI.removeScorebaord(player, board);
	}

	public void updateEntryTemporary(String key, String p)
	{
		updateEntry(key, p);
		entry.remove(getInt(key));
	}

	public void updateEntry(String key, String p)
	{
		updateEntry(key, p, "");
	}

	public void updateEntry(String key, String p, String p2)
	{
		int i = getInt(key);
		if(i == -1)
			return;

		display.removeEntry(entry.get(i));
		entry.put(i, addLinePl(i, p, p2));
	}

	protected String getString(String s)
	{
		return config.getString(s);
	}

	protected int getInt(String i)
	{
		return config.getConfig().getInt(key+i);
	}

	protected List<String> getStringList()
	{
		return config.getConfig().getStringList(key+"Lines");
	}

	protected DisplayEntry addLine(int i)
	{
		return display.addLine(TextFormat.colorize('&', getStringList().get(i)), line-i);
	}

	private DisplayEntry addLinePl(int i, String p, String p2)
	{
		return display.addLine(TextFormat.colorize('&', getStringList().get(i).replace("%m", p).replace("%n", p2)), line-i);
	}
}
