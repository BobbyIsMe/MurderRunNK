package com.joshuacc.mrnk.items;

import java.util.ArrayList;

import com.joshuacc.mrnk.lang.FormsLang;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public class ArmorMenu extends FormMenu {

	private static final ArrayList<ItemHelper> armor = new ArrayList<>();
	
	public static void registerArmor()
	{
		armor.add(new ArmorItem("hi"));
	}
	
	@Override
	public FormWindow createForm() {
		// TODO Auto-generated method stub
		FormWindowSimple menu = new FormWindowSimple(FormsLang.ARMORTITLE.toString(), FormsLang.ARMORDESC.toString());
		for(ItemHelper armor : armor)
		{
			menu.addElement(armor);
		}
		return menu;
	}
	
	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseSimple r = (FormResponseSimple) response;
		ItemHelper item = ItemHelper.getItem(((ItemHelper) r.getClickedButton()).getId());
		if(item != null)
		{
			item.itemResponse(player);
		}
	}
}
