package com.joshuacc.mrnk.scoreboards;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
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
		this.tips[map] = new TipBuilder(mapPrefix, board.getTip(mapPrefix, team.getMapOrigin()));
		this.tips[mode] = new TipBuilder(modePrefix, board.getTip(modePrefix, team.getMode()));
		this.tips[players] = new TipBuilder(playersPrefix, board.getTip(playersPrefix, team.getPlayers().size() + "/" + config.getMaximumPlayers()));
		this.tips[timeLimit] = new TipBuilder(timeLimitPrefix, board.getTip(timeLimitPrefix, format.format(1000 * config.getTimeLimit()).concat(" ").concat(board.getString("Time-Translation"))));
		this.tips[round] = new TipBuilder(roundPrefix, board.getTip(roundPrefix, team.getRound() + "/" + team.getPlayers().size()));
		this.tips[points] = new TipBuilder(pointsPrefix, board.getTip(pointsPrefix, mPlayer.getPlayerQueuedPoints()));
		this.tips[rank1] = new TipBuilder(rank1Prefix, board.getTip(rank1Prefix, na));
		this.tips[rank2] = new TipBuilder(rank2Prefix, board.getTip(rank2Prefix, na));
		this.tips[rank3] = new TipBuilder(rank3Prefix, board.getTip(rank3Prefix, na));
	}
}
