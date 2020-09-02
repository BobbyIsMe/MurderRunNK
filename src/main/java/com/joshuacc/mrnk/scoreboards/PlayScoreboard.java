package com.joshuacc.mrnk.scoreboards;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;

public class PlayScoreboard extends ScoreboardAbstract {

	public PlayScoreboard(Player player, MRMain main) 
	{
		super(player, "uwu", "Scoreboard-Play", playInt, main);
	}

	@Override
	protected void scoreboardStuff() 
	{
		MRTeam team = MRPlayer.getMRPlayer(player).getMapTeam();
		String q = "???";

		updateEntry("Time", TextUtils.getTimeFormat(MRPlayer.getMRPlayer(team.getKiller()).getPlayerTime()));
		updateEntry("Players", team.getSurvivors().size()+"");
		updateEntry("1", q, q);
		updateEntry("2", q, q);
		updateEntry("3", q, q);

		updateEntryTemporary("Killer", team.getKiller().getName());
		updateEntryTemporary("Map", team.getMapOrigin());
	}
}
