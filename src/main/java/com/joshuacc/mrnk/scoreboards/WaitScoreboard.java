package com.joshuacc.mrnk.scoreboards;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import de.theamychan.scoreboard.api.ScoreboardAPI;

public class WaitScoreboard extends ScoreboardAbstract {

	private static HashMap<Player,WaitScoreboard> scoreboard = new HashMap<>();

	public WaitScoreboard(Player player, MRMain main) {
		super(player, "uwo", "Scoreboard-Queue", main);
		scoreboard.put(player, this);
	}

	public static WaitScoreboard getPlayScoreboard(Player player)
	{
		return scoreboard.get(player);
	}

	@Override
	public void scoreboardStuff() 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");
		LocalDateTime now = LocalDateTime.now();
		//TODO: Make scoreboard lines
		addLine("");
	}
	
	public void removeScoreboard()
	{
		ScoreboardAPI.removeScorebaord(player, board);
		scoreboard.remove(player);
	}
}
