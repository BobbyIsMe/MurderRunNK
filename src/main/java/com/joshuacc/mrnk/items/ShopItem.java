package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.items.FormMenu.GameMenus;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;

public abstract class ShopItem extends ItemHelper {

	private final String name;
	private final String description;
	private final int price;
	private final int item;
	
	public ShopItem(String text, String name, String description, int price, int item) 
	{
		super(text);
		this.name = name;
		this.description = description;
		this.price = price;
		this.item = item;
	}
	
	public ShopItem(String text, ElementButtonImageData image, String name, String description, int price, int item) {
		super(text, image);
		this.name = name;
		this.description = description;
		this.price = price;
		this.item = item;
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
	
	public Item getItem()
	{
		return Item.get(item);
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
