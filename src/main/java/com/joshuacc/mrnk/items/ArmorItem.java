package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.items.FormMenu.GameMenus;
import com.joshuacc.mrnk.lang.FormsLang;

import cn.nukkit.form.element.ElementButtonImageData;

public class ArmorItem extends ShopItem
{

	public ArmorItem(int index, String text, ElementButtonImageData image, String name, String description, int price, int item)  {
		super(index, text, image, name, description, price, item, FormsLang.ALLCATEGORY.toString());
	}

	@Override
	public FormMenu getCategory() 
	{
		return GameMenus.ARMORMENU.getFormMenu();
	}
}
