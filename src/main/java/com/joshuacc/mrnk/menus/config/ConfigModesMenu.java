package com.joshuacc.mrnk.menus.config;

import com.joshuacc.mrnk.items.ItemHelper;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

public class ConfigModesMenu extends ConfigBackMenu 
{
	private final ItemHelper editNormal = new ItemHelper(TextFormat.colorize(FormsLang.LISTMODES.toString().replace("%s", MapModes.NORMAL.getMode()))) {

		@Override
		public void itemResponse(Player player) 
		{
			ConfigMapNormalModeMenu menu = (ConfigMapNormalModeMenu) GameMenus.CONFIGMAPNORMALMODEMENU.getFormMenu();
			((ConfigMenu) GameMenus.CONFIGMENU.getFormMenu()).setMode(player, menu);
			menu.open(player);
		}
		
	};
	
	public ConfigModesMenu(int id) 
	{
		super(id);
	}

	@Override
	public void backMenu(Player player) 
	{
		GameMenus.CONFIGMAPMENU.getFormMenu().open(player);
	}

	@Override
	public String getTitle(Player player) 
	{
		return FormsLang.LISTMODESTITLE.toString();
	}

	@Override
	public String getDesc() 
	{
		return FormsLang.LISTMODESDESC.toString();
	}

	@Override
	public void addItems(Player player, FormWindowSimple form) 
	{	
		form.addButton(editNormal);
	}

	@Override
	public void menuResponse(Player player, FormResponseSimple response) 
	{
		
	}

}
