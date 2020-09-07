package com.joshuacc.mrnk.scoreboards;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;

import cn.nukkit.Player;

public class WaitScoreboard extends ScoreboardAbstract {

	public WaitScoreboard(Player player, MRMain main) {
		super(player, "uwo", "Scoreboard-Queue", queueInt, main);
	}

	@Override
	public void scoreboardStuff() 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		MRTeam team = mPlayer.getMapTeam();
		MRArenasConfig config = team.getMapConfig();
		SimpleDateFormat format = new SimpleDateFormat("m");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");
		LocalDateTime now = LocalDateTime.now();

		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));

		updateEntry("QPts", mPlayer.getPlayerQueuedPoints()+"");
		updateEntry("Message", getString("Message-1"));

		updateEntryTemporary("Real Time", dtf.format(now));
		updateEntryTemporary("ID", team.getMapId());
		updateEntryTemporary("Map", team.getMapOrigin());
		updateEntryTemporary("Points", main.getMRPlayerConfig().getPoints(player)+"");
		updateEntryTemporary("Points Limit", config.getPointsLimit()+"");
		updateEntryTemporary("Mode", team.getMode());
		updateEntryTemporary("Time Limit", format.format(1000 * config.getTimeLimit()));
	}
}
