package com.joshuacc.mrnk.menus;

import com.joshuacc.mrnk.items.CategoryItem;
import com.joshuacc.mrnk.items.ItemHelper;
import com.joshuacc.mrnk.lang.FormsLang;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public class MurdererItemsMenu extends FormMenu {

	private ItemHelper traps = new CategoryItem(FormsLang.MURDITEMSTRAPS.toString(), GameMenus.MURDTRAPSMENU.getFormMenu());
	
	public MurdererItemsMenu(int id) {
		super(id);
	}

	@Override
	public FormWindow createForm(Player player) 
	{
		FormWindowSimple menu = new FormWindowSimple(FormsLang.MURDITEMSTITLE.toString(), FormsLang.MURDITEMSDESC.toString());
		menu.addElement(traps);
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
