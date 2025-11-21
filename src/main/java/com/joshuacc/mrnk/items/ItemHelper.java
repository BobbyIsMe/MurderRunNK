package com.joshuacc.mrnk.items;

import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;

public abstract class ItemHelper extends ElementButton {
	
	private static int globalId = 0;
	private static final HashMap<Integer, ItemHelper> items = new HashMap<>();
	
	private int id;
	
	public static ItemHelper getItem(int id)
	{
		return items.get(id);
	}
	
	public ItemHelper(String text) 
	{
		super(text);
		this.id = globalId++;
		items.put(id, this);
	}
	
	public ItemHelper(String text, ElementButtonImageData image) 
	{
		super(text, image);
	}

	public int getId()
	{
		return id;
	}
	
	public abstract void itemResponse(Player player);
}
