package com.joshuacc.mrnk.items;

import cn.nukkit.Player;

public class ArmorItem extends ItemHelper
{

	public ArmorItem(String text) {
		super(text);
	}

	@Override
	public void itemResponse(Player player) 
	{
		player.sendMessage(getText());
	}

}
