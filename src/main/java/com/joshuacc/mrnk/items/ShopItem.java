package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.items.FormMenu.GameMenus;

import cn.nukkit.Player;

public abstract class ShopItem extends ItemHelper {

	private final String name;
	private final String description;
	private final int price;
	
	public ShopItem(String text, String name, String description, int price) 
	{
		super(text);
		this.name = name;
		this.description = description;
		this.price = price;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public abstract FormMenu getCategory();

	@Override
	public void itemResponse(Player player) 
	{
		SelectItemMenu menu = (SelectItemMenu) GameMenus.SELITEMMENU.getFormMenu();
		menu.addItemToPlayer(player, this);
		menu.open(player);
	}

}
