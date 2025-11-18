package com.joshuacc.mrnk.scoreboards;

import java.util.List;

import com.joshuacc.mrnk.files.MRScoreboardConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam;

import cn.nukkit.Player;

public abstract class ScoreboardAbstract {

	protected static final int queueSize = 9;
	protected static final int playSize = 5;
	protected static final String[] queuePrefix = new String[queueSize];
	protected static final String[] playPrefix = new String[playSize];
	
	protected MRMain main;
	protected TipBuilder[] tips;
	protected MRScoreboardConfig board;
	protected Player player;
	protected MRTeam team;

	private String key;

	public ScoreboardAbstract(Player player, String key, TipBuilder[] tips, MRMain main)
	{
		this.main = main;
		this.player = player;
		this.key = key+".";
		this.board = main.getMRScoreboardConfig();
		this.tips = tips;
	}
	
	public ScoreboardAbstract(MRTeam team, String key, TipBuilder[] tips, MRMain main)
	{
		this.main = main;
		this.team = team;
		this.key = key+".";
		this.board = main.getMRScoreboardConfig();
		this.tips = tips;
	}
	
	public static void registerScoreboard(MRScoreboardConfig b)
	{
		String queue = "Queue.";
		int qMap = b.getInt(queue+"Map");
		int qMode = b.getInt(queue+"Mode");
		int qPlayers = b.getInt(queue+"Players");
		int qTimeLimit = b.getInt(queue+"Time Limit");
		int qRound = b.getInt(queue+"Round");
		int qPoints = b.getInt(queue+"Points");
		int qRank1 = b.getInt(queue+"Rank-1");
		int qRank2 = b.getInt(queue+"Rank-2");
		int qRank3 = b.getInt(queue+"Rank-3");
		queuePrefix[qMap] = "q_"+qMap;
		queuePrefix[qMode] = "q_"+qMode;
		queuePrefix[qPlayers] = "q_"+qPlayers;
		queuePrefix[qTimeLimit] = "q_"+qTimeLimit;
		queuePrefix[qRound] = "q_"+qRound;
		queuePrefix[qPoints] = "q_"+qPoints;
		queuePrefix[qRank1] = "q_"+qRank1;
		queuePrefix[qRank2] = "q_"+qRank2;
		queuePrefix[qRank3] = "q_"+qRank3;
		
		String play = "Play.";
		int pMap = b.getInt(play+"Map");
		int pTimer = b.getInt(play+"Timer");
		int pMode = b.getInt(play+"Mode");
		int pKiller = b.getInt(play+"Killer");
		int pSL = b.getInt(play+"Survivors Left");
		
		playPrefix[pMap] = "p_"+pMap;
		playPrefix[pTimer] = "p_"+pTimer;
		playPrefix[pMode] = "p_"+pMode;
		playPrefix[pKiller] = "p_"+pKiller;
		playPrefix[pSL] = "p_"+pSL;
	}

	protected abstract void scoreboardStuff();

	public void openScoreboard()
	{
		scoreboardStuff();
	}

	public void updateEntry(int index, String p)
	{
		if(index >= 0 && index < tips.length)
		{
			TipBuilder tip = tips[index];
			tip.setCurrentTip(board.getTip(tip.getPrefix(), p));
		}
	}
	
	public void updateEntry(int index, int p)
	{
		updateEntry(index, Integer.toString(p));
	}

	public String getString(String s)
	{
		StringBuilder str = new StringBuilder(key);
		return board.getString(str.append(s).toString());
	}

	public int getInt(String s)
	{
		StringBuilder str = new StringBuilder(key);
		return board.getInt(str.append(s).toString());
	}

	protected List<String> getStringList()
	{
		return board.getConfig().getStringList(key+"Lines");
	}
	
	protected void addTip(int index, TipBuilder tipBuilder)
	{
		if(index >= 0 && index < tips.length)
		this.tips[index] = tipBuilder;
	}
	
	public void sendScoreboardTip(Player player, String stop) 
	{
		StringBuilder string = new StringBuilder();
		for (int i = 0; i < tips.length; i++) 
		{
			TipBuilder tip = tips[i];
			if(!tip.getCurrentTip().equals(tip.getPreviousTip())) 
			{
				string.append(tip.getCurrentTip());
				tips[i].setPreviousTip(tip.getCurrentTip());
			} else {
				string.append(main.getEmpty());
			}
		}

		player.sendTip(string.append(stop).toString());
	}
}

class TipBuilder
{
	private String prefix;
	private String currentTip;
	private String previousTip;
	
	public TipBuilder(String prefix, String currentTip)
	{
		this.prefix = prefix;
		this.currentTip = currentTip;
		this.previousTip = "";
	}
	
	public String getPrefix() 
	{
		return prefix;
	}

	public void setCurrentTip(String currentTip)
	{
		this.currentTip = currentTip;
	}
	
	public void setPreviousTip(String previousTip)
	{
		this.previousTip = previousTip;
	}
	
	public String getCurrentTip()
	{
		return this.currentTip;
	}
	
	public String getPreviousTip()
	{
		return this.previousTip;
	}
}