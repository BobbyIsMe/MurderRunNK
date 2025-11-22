package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.lang.FormsLang;

public class ArmorMenu extends ItemMenu {
	
	public ArmorMenu(int id)
	{
		super(id);
		registerItem(new ArmorItem("hi", "hello", "why", 500));
	}

	@Override
	public String getTitle() 
	{
		return FormsLang.ARMORTITLE.toString();
	}

	@Override
	public String getDesc() 
	{
		return FormsLang.ARMORDESC.toString();
	}
}
