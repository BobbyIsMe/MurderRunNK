package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.events.PlayerJoinGameEvent;
import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.utils.MapState;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;

public class SelectMapItem extends ItemHelper 
{
	private final String map;
	private final MapModes mode;
	
	public SelectMapItem(String text, String map, MapModes mode) 
	{
		super(text);
		this.map = map;
		this.mode = mode;
	}

	@Override
	public void itemResponse(Player player) 
	{
		MRMain main = MRMain.getInstance();
		MRArenasConfig config = main.getMapConfigs().get(map);
		int multiples = config.getMultiples(mode.getMode());

		if(main.getMapConfigs().get(map).isMapEnabled() && MRPlayer.getMRPlayer(player) == null)
			for(int i = 1; i <= multiples; i++)
			{
				MRTeam team = MRTeam.getMapTeamByID(map, i, mode);
				if(team.getState() == MapState.READY || team.getState() == MapState.STARTING)
				{
					PlayerJoinGameEvent join = new PlayerJoinGameEvent(player, team);
					player.sendMessage(TextUtils.format(TextUtils.formatLevel(ConfigLang.SUCCESSJOIN.toString(), map)));
					Server.getInstance().getPluginManager().callEvent(join);
					break;
				}
			}

		if(MRPlayer.getMRPlayer(player) == null)
			player.sendMessage(TextUtils.format(ConfigLang.NOTAVAILABLEMAP.toString()));
	}

}
