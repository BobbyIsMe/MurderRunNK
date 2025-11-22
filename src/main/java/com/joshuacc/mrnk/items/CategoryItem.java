package com.joshuacc.mrnk.items;

import cn.nukkit.Player;

public class CategoryItem extends ItemHelper {

	private FormMenu menu;
	
	public CategoryItem(String text, FormMenu menu) 
	{
		super(text);
		this.menu = menu;
	}

	@Override
	public void itemResponse(Player player) 
	{
		menu.open(player);
	}
}
