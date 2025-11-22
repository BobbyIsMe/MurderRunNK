package com.joshuacc.mrnk.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;

public abstract class ItemHelper extends ElementButton {
	
	public ItemHelper(String text) 
	{
		super(text);
	}
	
	public ItemHelper(String text, ElementButtonImageData image) 
	{
		super(text, image);
	}
	
	public abstract void itemResponse(Player player);
}
