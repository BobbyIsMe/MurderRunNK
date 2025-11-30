package com.joshuacc.mrnk.menus;

import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.items.SurvivorTrapItem;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTraps;

import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.utils.TextFormat;

public class SurvivorTrapsMenu extends ItemMenu {

	public SurvivorTrapsMenu(int id) 
	{
		super(id);
		MRTrapsConfig config = MRMain.getInstance().getMRTrapsConfig();
		int i = 0;
		for(MRTraps trap : MRTraps.getTraps(true))
		{
			String name = trap.getName();
			String trapName = config.getString(name, "Name");
			int price = config.getInt(name, "Price");
			registerItem(new SurvivorTrapItem(i,
					TextFormat.colorize(FormsLang.ITEMNAME.toString().replace("%s1", trapName).replace("%s2", TextFormat.colorize(FormsLang.ITEMPRICE.toString().replace("%n", Integer.toString(price))))),
					new ElementButtonImageData(config.getString(name, "Path"), config.getString(name, "Image")),
			        trapName,
			        config.getString(name, "Description"),
			        price,
			        config.getInt(name, "Item"), config.getInt(name, "Meta"), trap.getType(), trap.isStackable()));
			i++;
		}
	}

	@Override
	public String getTitle() 
	{
		return FormsLang.TRAPSTITLE.toString();
	}

	@Override
	public String getDesc() 
	{
		return TextFormat.colorize(FormsLang.TRAPSDESC.toString().replace("%s", FormsLang.SURVTRAPSDESC.toString()));
	}

	@Override
	public FormMenu getOrigin() 
	{
		return GameMenus.SURVITEMSMENU.getFormMenu();
	}
}
