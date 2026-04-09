package com.joshuacc.mrnk.menus.config;

import com.joshuacc.mrnk.items.ItemHelper;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.menus.FormMenu;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public abstract class ConfigBackMenu extends FormMenu {

	protected final ItemHelper backButton = new ItemHelper(FormsLang.EDITEXIT.toString()) {

		@Override
		public void itemResponse(Player player) {
			backMenu(player);
		}
		
	};
	
	public ConfigBackMenu(int id) 
	{
		super(id);
	}
	
	
	@Override
	public FormWindow createForm(Player player) 
	{
		FormWindowSimple configMenu = new FormWindowSimple(getTitle(player), getDesc());
		configMenu.addButton(backButton);
		addItems(player, configMenu);
		return configMenu;
	}
	
	@Override
	public void response(Player player, FormResponse response)
	{
		FormResponseSimple r = (FormResponseSimple) response;
		if(r == null)
			return;
		
		ElementButton button = r.getClickedButton();
		if(button instanceof ItemHelper)
		{
			((ItemHelper) button).itemResponse(player);
		}
		
		menuResponse(player, r);
	}
	
	public abstract void backMenu(Player player);
	public abstract String getTitle(Player player);
	public abstract String getDesc();
	public abstract void addItems(Player player, FormWindowSimple form);
	public abstract void menuResponse(Player player, FormResponseSimple response);
}
