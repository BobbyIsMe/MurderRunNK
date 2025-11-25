package com.joshuacc.mrnk.menus;

import java.util.ArrayList;
import java.util.List;

import com.joshuacc.mrnk.items.ArmorItem;
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
		List<String> keys = new ArrayList<>(armor.getKeys(false));
		for (int i = 0; i < keys.size(); i++) {
		    String item = keys.get(i);

		    ConfigSection itemSection = armor.getSection(item);

		    String name = TextFormat.colorize(itemSection.getString("Name"));
		    int price = itemSection.getInt("Price");

		    registerItem(new ArmorItem(
		    	i,
		        TextFormat.colorize(FormsLang.ITEMNAME.toString().replace("%s1", name).replace("%s2", TextFormat.colorize(FormsLang.ITEMPRICE.toString().replace("%n", Integer.toString(price))))),
		        new ElementButtonImageData(itemSection.getString("Path"), itemSection.getString("Image")),
		        TextFormat.colorize(name),
		        itemSection.getString("Description"),
		        price,
		        itemSection.getInt("Item")
		    ));
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
