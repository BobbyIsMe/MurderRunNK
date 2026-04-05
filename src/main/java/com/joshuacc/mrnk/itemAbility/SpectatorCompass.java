package com.joshuacc.mrnk.itemAbility;

import com.joshuacc.mrnk.menus.FormMenu.GameMenus;

import cn.nukkit.Player;

public class SpectatorCompass implements ItemAbility {

	@Override
	public void itemAbility(Player player) 
	{
		GameMenus.SPECTATORMENU.getFormMenu().open(player);
	}

}
