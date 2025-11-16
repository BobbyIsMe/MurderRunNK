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
		super(player, "Queue", new TipBuilder[6], main);
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
		
		String mapPrefix = queuePrefix[map];
		String modePrefix = queuePrefix[mode];
		String playersPrefix = queuePrefix[players];
		String timeLimitPrefix = queuePrefix[timeLimit];
		String roundPrefix = queuePrefix[round];
		String pointsPrefix = queuePrefix[points];
		
		this.tips[map] = new TipBuilder(mapPrefix, board.getTip(mapPrefix, team.getMapOrigin()));
		this.tips[mode] = new TipBuilder(modePrefix, board.getTip(modePrefix, team.getMode()));
		this.tips[players] = new TipBuilder(playersPrefix, board.getTip(playersPrefix, team.getPlayers().size() + "/" + config.getMaximumPlayers()));
		this.tips[timeLimit] = new TipBuilder(timeLimitPrefix, board.getTip(timeLimitPrefix, format.format(1000 * config.getTimeLimit()).concat(" ").concat(board.getString("Time-Translation"))));
		this.tips[round] = new TipBuilder(roundPrefix, board.getTip(roundPrefix, team.getRound() + "/" + team.getPlayers().size()));
		this.tips[points] = new TipBuilder(pointsPrefix, board.getTip(pointsPrefix, mPlayer.getPlayerQueuedPoints()));
		player.sendMessage(mode+"");
	}
}
