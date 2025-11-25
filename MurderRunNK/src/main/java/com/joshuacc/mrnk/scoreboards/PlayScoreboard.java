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
		
		addTip(map, new TipBuilder(mapPrefix, board.getTip(mapPrefix, TextUtils.formatLine(getString("Map-Line"), team.getMapOrigin()))));
		addTip(timer, new TipBuilder(timerPrefix, board.getTip(timerPrefix, TextUtils.formatLine(getString("Timer-Line"), TextUtils.getTimeFormat(0)))));
		addTip(mode, new TipBuilder(modePrefix, board.getTip(modePrefix, TextUtils.formatLine(getString("Mode-Line"), team.getMode().toString()))));
		addTip(killer, new TipBuilder(killerPrefix, board.getTip(killerPrefix, TextUtils.formatLine(getString("Killer-Line"), team.getKiller().getName()))));
		addTip(sL, new TipBuilder(sLPrefix, board.getTip(sLPrefix, TextUtils.formatLine(getString("Survivors Left-Line"), Integer.toString(team.getSurvivors().size())))));
	}
}
