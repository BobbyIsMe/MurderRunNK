package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;

public class SpectatorItem extends ItemHelper 
{

	private final String target;
	
	public SpectatorItem(String target) 
	{
		super(TextUtils.format(FormsLang.SPECTATORPLAYER.toString().replace("%p", target)));
		this.target = target;
	}
	
	public Player getTarget()
	{
		return MRMain.getInstance().getServer().getPlayer(target);
	}

	@Override
	public void itemResponse(Player player) 
	{
		player.teleport(getTarget());
	}

}
