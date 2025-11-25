package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.menus.FormMenu;
import com.joshuacc.mrnk.menus.FormMenu.GameMenus;

import cn.nukkit.form.element.ElementButtonImageData;

public class ArmorItem extends ShopItem
{

	public ArmorItem(int index, String text, ElementButtonImageData image, String name, String description, int price, int item)  {
		super(index, text, image, name, description, price, item, FormsLang.ALLCATEGORY.toString(), false);
	}

	@Override
	public FormMenu getCategory() 
	{
		return GameMenus.ARMORMENU.getFormMenu();
	}
}
