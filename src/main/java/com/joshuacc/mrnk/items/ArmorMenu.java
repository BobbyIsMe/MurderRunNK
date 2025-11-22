package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;

public class ArmorMenu extends ItemMenu {
	
	public ArmorMenu(int id)
	{
		super(id);
		ConfigSection armor = MRMain.getInstance().getMRItemShopConfig().getAllItemsByType("Armor");
		for(String item : armor.getKeys(false))
		{
			ConfigSection itemSection = armor.getSection(item);
			String name = TextFormat.colorize(itemSection.getString("Name"));
			itemSection.getString("path");
			registerItem(new ArmorItem(name, new ElementButtonImageData(
					itemSection.getString("Path"), itemSection.getString("Image")), 
					name, 
					itemSection.getString("Description"), 
					itemSection.getInt("Price"),
					itemSection.getInt("Item")));
		}
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

	@Override
	public FormMenu getOrigin() 
	{
		return GameMenus.SURVITEMSMENU.getFormMenu();
	}
}
