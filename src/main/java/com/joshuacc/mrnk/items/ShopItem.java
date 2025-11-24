package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.menus.FormMenu;
import com.joshuacc.mrnk.menus.SelectItemMenu;
import com.joshuacc.mrnk.menus.FormMenu.GameMenus;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;

public abstract class ShopItem extends ItemHelper {

	private final int index;
	private final String name;
	private final String description;
	private final String category;
	private final int price;
	private final int item;
	
	public ShopItem(int index, String text, String name, String description, int price, int item, String category) 
	{
		super(text);
		this.index = index;
		this.name = name;
		this.description = description;
		this.price = price;
		this.item = item;
		this.category = category;
	}
	
	public ShopItem(int index, String text, ElementButtonImageData image, String name, String description, int price, int item, String category) {
		super(text, image);
		this.index = index;
		this.name = name;
		this.description = description;
		this.price = price;
		this.item = item;
		this.category = category;
	}
	
	public int getIndex()
	{
		return index;
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
	
	public String getType()
	{
		return category;
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
