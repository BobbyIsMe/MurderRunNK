package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.menus.FormMenu;
import com.joshuacc.mrnk.menus.FormMenu.GameMenus;

import cn.nukkit.form.element.ElementButtonImageData;

public class SurvivorTrapItem  extends TrapItem {

	public SurvivorTrapItem(int index, String text, ElementButtonImageData image, String name, String description, int price,
			int item, int meta, String category, boolean stackable, String trapName) {
		super(index, text, image, name, description, price, item, meta, category, stackable, trapName);
	}

	@Override
	public FormMenu getCategory() 
	{
		return GameMenus.SURVTRAPSMENU.getFormMenu();
	}
}
