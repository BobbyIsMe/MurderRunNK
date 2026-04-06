package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.menus.FormMenu;
import com.joshuacc.mrnk.menus.FormMenu.GameMenus;
import com.joshuacc.mrnk.perks.PerkBase;

import cn.nukkit.form.element.ElementButtonImageData;

public class SurvivorPerkItem extends PerkItem {
	
	public SurvivorPerkItem(int index, String text, ElementButtonImageData image, String name, String description,
			int price, int item, int meta, PerkBase perk) {
		super(index, text, image, name, description, price, item, meta, perk);
	}

	@Override
	public FormMenu getCategory() 
	{
		return GameMenus.SURVITEMSMENU.getFormMenu();
	}
}
