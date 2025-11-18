package com.joshuacc.mrnk.scoreboards;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;

public class WaitScoreboard extends ScoreboardAbstract {

	public WaitScoreboard(Player player, MRMain main) {
		super(player, "Queue", new TipBuilder[queueSize], main);
	}

	@Override
	public void scoreboardStuff() 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		MRTeam team = mPlayer.getMapTeam();
		MRArenasConfig config = team.getMapConfig();
		SimpleDateFormat format = new SimpleDateFormat("m");
		
		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));

		int map = getInt("Map");
		int mode = getInt("Mode");
		int players = getInt("Players");
		int timeLimit = getInt("Time Limit");
		int round = getInt("Round");
		int points = getInt("Points");
		int rank1 = getInt("Rank-1");
		int rank2 = getInt("Rank-2");
		int rank3 = getInt("Rank-3");
		
		String mapPrefix = queuePrefix[map];
		String modePrefix = queuePrefix[mode];
		String playersPrefix = queuePrefix[players];
		String timeLimitPrefix = queuePrefix[timeLimit];
		String roundPrefix = queuePrefix[round];
		String pointsPrefix = queuePrefix[points];
		String rank1Prefix = queuePrefix[rank1];
		String rank2Prefix = queuePrefix[rank2];
		String rank3Prefix = queuePrefix[rank3];
		
		String na = board.getString("NA-Translation");
		addTip(map, new TipBuilder(mapPrefix, board.getTip(mapPrefix, TextUtils.formatLine(getString("Map-Line"),  team.getMapOrigin()))));
		addTip(mode,  new TipBuilder(modePrefix, board.getTip(modePrefix, TextUtils.formatLine(getString("Mode-Line"), team.getMode()))));
		addTip(players, new TipBuilder(playersPrefix, board.getTip(playersPrefix, TextUtils.formatLine(getString("Players-Line"), Integer.toString(team.getPlayers().size()), Integer.toString(config.getMaximumPlayers())))));
		addTip(timeLimit,  new TipBuilder(timeLimitPrefix, board.getTip(timeLimitPrefix, TextUtils.formatLine(getString("Time Limit-Line"), format.format(1000 * config.getTimeLimit())))));
		addTip(round, new TipBuilder(roundPrefix, board.getTip(roundPrefix, TextUtils.formatLine(getString("Round-Line"), Integer.toString(team.getRound()), Integer.toString(team.getPlayers().size())))));
		addTip(points, new TipBuilder(pointsPrefix, board.getTip(pointsPrefix, TextUtils.formatLine(getString("Points-Line"), Integer.toString(mPlayer.getPlayerQueuedPoints())))));
		addTip(rank1, new TipBuilder(rank1Prefix, board.getTip(rank1Prefix, na)));
		addTip(rank2, new TipBuilder(rank2Prefix, board.getTip(rank2Prefix, na)));
		addTip(rank3, new TipBuilder(rank3Prefix, board.getTip(rank3Prefix, na)));
	}
}
