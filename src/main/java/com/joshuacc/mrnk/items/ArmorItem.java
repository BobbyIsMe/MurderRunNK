package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.items.FormMenu.GameMenus;

public class ArmorItem extends ShopItem
{

	public ArmorItem(String text, String name, String description, int price)  {
		super(text, name, description, price);
	}

	@Override
	public FormMenu getCategory() 
	{
		return GameMenus.ARMORMENU.getFormMenu();
	}
}
