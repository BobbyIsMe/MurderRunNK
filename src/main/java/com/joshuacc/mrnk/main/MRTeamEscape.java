package com.joshuacc.mrnk.main;

import com.joshuacc.mrnk.files.MRArenasConfig;

public class MRTeamEscape extends MRTeam {

	public MRTeamEscape(MRMain main, String map, MRArenasConfig config, int multiple) 
	{
		super(main, map, MapModes.ESCAPE, config, multiple);
	}
}
