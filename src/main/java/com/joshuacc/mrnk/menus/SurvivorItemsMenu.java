package com.joshuacc.mrnk.menus;

import com.joshuacc.mrnk.items.CategoryItem;
import com.joshuacc.mrnk.items.ItemHelper;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.menus.FormMenu.GameMenus;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public class SurvivorItemsMenu extends FormMenu {

	private ItemHelper traps = new CategoryItem(FormsLang.SURVITEMSTRAPS.toString(), GameMenus.SURVTRAPSMENU.getFormMenu());
	private ItemHelper armor = new CategoryItem(FormsLang.SURVITEMSARMOR.toString(), GameMenus.ARMORMENU.getFormMenu());
	private ItemHelper perks = new CategoryItem(FormsLang.ITEMSPERKS.toString(), GameMenus.SURVPERKSMENU.getFormMenu());
	private ItemHelper utility = new CategoryItem(FormsLang.SURVITEMSUTIL.toString(), null);
	
	public SurvivorItemsMenu(int id) {
		super(id);
	}

	@Override
	public FormWindow createForm(Player player) 
	{
		FormWindowSimple menu = new FormWindowSimple(FormsLang.SURVITEMSTITLE.toString(), FormsLang.SURVITEMSDESC.toString());
		menu.addElement(armor);
		menu.addElement(traps);
		menu.addElement(perks);
		return menu;
	}

	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseSimple r = (FormResponseSimple) response;
		if(r == null)
			return;
		
		if(r.getClickedButton() instanceof ItemHelper)
		{
			((ItemHelper) r.getClickedButton()).itemResponse(player);
		}
	}

}
