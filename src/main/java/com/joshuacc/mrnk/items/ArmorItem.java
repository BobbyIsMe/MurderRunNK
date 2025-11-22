package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.items.FormMenu.GameMenus;

import cn.nukkit.form.element.ElementButtonImageData;

public class ArmorItem extends ShopItem
{

	public ArmorItem(String text, ElementButtonImageData image, String name, String description, int price, int item)  {
		super(text, image, name, description, price, item);
	}

	@Override
	public FormMenu getCategory() 
	{
		return GameMenus.ARMORMENU.getFormMenu();
	}
}
