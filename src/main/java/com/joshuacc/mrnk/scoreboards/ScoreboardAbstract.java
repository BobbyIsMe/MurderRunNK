package com.joshuacc.mrnk.scoreboards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplayEntry;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import de.theamychan.scoreboard.network.SortOrder;

public abstract class ScoreboardAbstract {
	
	protected Player player;
	protected Scoreboard board;
	protected ScoreboardDisplay display;
	
	protected ArrayList<Integer> integers;
	protected HashMap<Integer,DisplayEntry> entry;
	
	private MRScoreboardConfig config;
	private String key;
	private int line;
	
	public ScoreboardAbstract(Player player, String objName, String key, MRMain main)
	{
		this.integers = new ArrayList<>();
		this.entry = new HashMap<>();
		this.player = player;
		this.key = key+".";
		this.board = ScoreboardAPI.createScoreboard();
		this.config = main.getMRScoreboardConfig();
		this.display = board.addDisplay(DisplaySlot.SIDEBAR, objName, TextFormat.colorize(config.getScoreboardTitle()), SortOrder.DESCENDING);
		this.line = getStringList().size()-1;
	}
	
	public abstract void scoreboardStuff();
	
	public void openScoreboard()
	{
		scoreboardStuff();
		integers = null;
		ScoreboardAPI.setScoreboard(player, board);
	}
	
	public int getInt(String i)
	{
		return config.getConfig().getInt(key+i);
	}
	
	public String getString(String s)
	{
		return config.getConfig().getString("Scoreboard-"+s);
	}
	
	public List<String> getStringList()
	{
		return config.getConfig().getStringList(key+"Lines");
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
	
	public DisplayEntry addLine(int i)
	{
		return display.addLine(TextFormat.colorize('&', getStringList().get(i)), line-i);
	}
	
	private DisplayEntry addLinePl(int i, String p, String p2)
	{
		return display.addLine(TextFormat.colorize('&', getStringList().get(i).replace("%m", p).replace("%n", p2)), line-i);
	}
}
