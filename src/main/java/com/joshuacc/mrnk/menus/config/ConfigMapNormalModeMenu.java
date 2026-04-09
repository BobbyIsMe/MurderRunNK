package com.joshuacc.mrnk.menus.config;

import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.menus.FormMenu;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponseSimple;

public class ConfigMapNormalModeMenu extends ConfigMapModeMenu {

	public ConfigMapNormalModeMenu(int id, MapModes mode) 
	{
		super(id, mode);
	}

	@Override
	public FormMenu getEditMap() 
	{
		return GameMenus.CONFIGMAPNORMALSETTINGSMENU.getFormMenu();
	}

	@Override
	public void configureMap(Player player, FormResponseSimple response) 
	{
		
	}
}
