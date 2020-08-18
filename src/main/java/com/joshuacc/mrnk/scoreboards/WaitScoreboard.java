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
		super(player, "uwo", "Scoreboard-Queue", main);
		integers.add(getInt("Real Time"));
		integers.add(getInt("ID"));
		integers.add(getInt("Map"));
		integers.add(getInt("Players"));
		integers.add(getInt("Points"));
		integers.add(getInt("Points Limit"));
		integers.add(getInt("QPts"));
		integers.add(getInt("Message"));
		integers.add(getInt("Mode"));
		integers.add(getInt("Time Limit"));
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
		
		for(int i = 0; i < getStringList().size(); i++)
			if(!integers.contains(i))
				addLine(i);
			else
				entry.put(i, addLine(i));
		
		updateEntry("Real Time", dtf.format(now));
		updateEntry("ID", team.getMapId());
		updateEntry("Map", team.getMapOrigin());
		updateEntry("Points", mPlayer.getPlayerConfig().getPoints(player)+"");
		updateEntry("Points Limit", config.getPointsLimit()+"");
		updateEntry("QPts", mPlayer.getPlayerQueuedPoints()+"");
		updateEntry("Message", getString("Message-1"));
		updateEntry("Mode", team.getMode());
		updateEntry("Time Limit", format.format(1000 * config.getTimeLimit()));
	}
}
