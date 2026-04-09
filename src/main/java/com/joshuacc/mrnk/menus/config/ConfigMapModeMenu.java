package com.joshuacc.mrnk.menus.config;

import java.util.ArrayList;
import com.joshuacc.mrnk.items.ItemHelper;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.menus.FormMenu;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;

public abstract class ConfigMapModeMenu extends ConfigBackMenu {

	private static final ElementButton editMap = new ElementButton(FormsLang.EDITMAP.toString());

	private ArrayList<ItemHelper> items;
	private MapModes mode;
	
	public ConfigMapModeMenu(int id, MapModes mode) 
	{
		super(id);
		items = new ArrayList<>();
	}
	
	public MapModes getMapMode()
	{
		return mode;
	}
	
	public void registerItems(ItemHelper helper)
	{
		items.add(helper);
	}
	
	@Override
	public String getTitle(Player player) 
	{
		ConfigMenu menu = (ConfigMenu) GameMenus.CONFIGMENU.getFormMenu();
		return FormsLang.CONMAPMODETITLE.toString().replace("%l", menu.getMap(player)).replace("%l", menu.getMode(player).getMode());
	}

	@Override
	public String getDesc() 
	{
		return null;
	}
	
	@Override
	public void addItems(Player player, FormWindowSimple form)
	{
		for(ItemHelper item : items)
		{
			form.addButton(item);
		}
	}
	
	@Override
	public void menuResponse(Player player, FormResponseSimple response)
	{
		ElementButton button = response.getClickedButton();
		 if(button.getText().equals(editMap.getText()))
		 {
			 getEditMap().open(player);
		 }
		 configureMap(player, response);
	}
	
	@Override
	public void backMenu(Player player) 
	{
		ConfigMenu menu = (ConfigMenu) GameMenus.CONFIGMENU.getFormMenu();
		menu.setMode(player, null);
		menu.open(player);
	}
	
	public abstract FormMenu getEditMap();
	public abstract void configureMap(Player player, FormResponseSimple response);
}
