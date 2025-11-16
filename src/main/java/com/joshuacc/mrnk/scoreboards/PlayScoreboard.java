package com.joshuacc.mrnk.scoreboards;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.TextUtils;

public class PlayScoreboard extends ScoreboardAbstract {

	public PlayScoreboard(MRTeam team, MRMain main) 
	{
		super(team, "Play", new TipBuilder[playSize], main);
	}

	@Override
	protected void scoreboardStuff() 
	{
		int map = getInt("Map");
		int timer = getInt("Timer");
		int mode = getInt("Mode");
		int killer = getInt("Killer");
		int sL = getInt("Survivors Left");
		
		String mapPrefix = playPrefix[map];
		String timerPrefix = playPrefix[timer];
		String modePrefix = playPrefix[mode];
		String killerPrefix = playPrefix[killer];
		String sLPrefix = playPrefix[sL];
		
		this.tips[map] = new TipBuilder(mapPrefix, board.getTip(mapPrefix, team.getMapOrigin()));
		this.tips[timer] = new TipBuilder(timerPrefix, board.getTip(timerPrefix, TextUtils.getTimeFormat(0)));
		this.tips[mode] = new TipBuilder(modePrefix, board.getTip(modePrefix, team.getMode().toString()));
		this.tips[killer] = new TipBuilder(killerPrefix, board.getTip(killerPrefix, team.getKiller().getName()));
		this.tips[sL] = new TipBuilder(sLPrefix, board.getTip(sLPrefix, team.getSurvivors().size()));
	}
}
