package com.joshuacc.mrnk.menus;

import com.joshuacc.mrnk.files.MRPerksConfig;
import com.joshuacc.mrnk.items.MurdererPerkItem;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPerks;
import com.joshuacc.mrnk.perks.PerkBase;

import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.utils.TextFormat;

public class MurdererPerksMenu extends ItemMenu {

	public MurdererPerksMenu(int id) 
	{
		super(id);
		MRPerksConfig config = MRMain.getInstance().getMRPerksConfig();
		int item = config.getItem();
		int meta = config.getMeta();
		int i = 0;
		for(MRPerks perk : MRPerks.getPerks(false))
		{
			String name = perk.getName();
			String perkName = config.getString(name, "Name");
			int price = config.getInt(name, "Price");
			registerItem(new MurdererPerkItem(i,
					TextFormat.colorize(FormsLang.ITEMNAME.toString().replace("%s1", perkName).replace("%s2", TextFormat.colorize(FormsLang.ITEMPRICE.toString().replace("%n", Integer.toString(price))))),
					new ElementButtonImageData(config.getString(name, "Path"), config.getString(name, "Image")),
			        perkName,
			        config.getString(name, "Description"),
			        price,
			        item, meta, (PerkBase) perk));
			i++;
		}
	}

	@Override
	public String getTitle() 
	{
		return FormsLang.PERKSTITLE.toString();
	}

	@Override
	public String getDesc() 
	{
		return TextFormat.colorize(FormsLang.PERKSDESC.toString());
	}

	@Override
	public FormMenu getOrigin() 
	{
		return GameMenus.MURDITEMSMENU.getFormMenu();
	}
}
