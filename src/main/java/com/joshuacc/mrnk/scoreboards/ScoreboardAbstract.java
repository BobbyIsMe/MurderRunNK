package com.joshuacc.mrnk.scoreboards;

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
	private MRScoreboardConfig config;
	private String key;
	private int line;
	
	public ScoreboardAbstract(Player player, String objName, String key, MRMain main)
	{
		this.player = player;
		this.key = key+".";
		this.board = ScoreboardAPI.createScoreboard();
		this.config = main.getMRScoreboardConfig();
		this.display = board.addDisplay(DisplaySlot.SIDEBAR, objName, config.getScoreboardTitle(), SortOrder.DESCENDING);
		this.line = getStringList().size();
	}
	
	public abstract void scoreboardStuff();
	
	public void openScoreboard()
	{
		scoreboardStuff();
		ScoreboardAPI.setScoreboard(player, board);
	}
	
	public int getInt(String i)
	{
		return config.getConfig().getInt(key+i);
	}
	
	public String getString(String s)
	{
		return config.getConfig().getString(key+s);
	}
	
	public List<String> getStringList()
	{
		return config.getConfig().getStringList(key+"Lines");
	}
	
	public void addLine(String s)
	{
		display.addLine(TextFormat.colorize('&', s), line);
		line--;
	}
	
	public DisplayEntry addLinePl(String s, String p)
	{
		DisplayEntry entry = display.addLine(TextFormat.colorize('&', s.replace("%m", p)), line);
		line--;
		return entry;
	}
	
	public DisplayEntry addLineDupe(String s, String p, String p2)
	{
		DisplayEntry entry = display.addLine(TextFormat.colorize('&', s.replace("%m", p).replace("%m2", p2)), line);
		line--;
		return entry;
	}
}
