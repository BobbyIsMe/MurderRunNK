package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.menus.FormMenu.GameMenus;
import com.joshuacc.mrnk.menus.config.ConfigMapMenu;

import cn.nukkit.Player;

public class MapItem extends ItemHelper {

	private final String map;
	public MapItem(String text, String map) 
	{
		super(text);
		this.map = map;
	}
	
	public String getMap()
	{
		return map;
	}

	@Override
	public void itemResponse(Player player) 
	{
		ConfigMapMenu menu = (ConfigMapMenu) GameMenus.CONFIGMAPMENU.getFormMenu();
		menu.open(player);
	}

}
