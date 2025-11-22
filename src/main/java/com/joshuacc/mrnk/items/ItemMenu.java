package com.joshuacc.mrnk.items;

import java.util.ArrayList;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public abstract class ItemMenu extends FormMenu {

	private final ArrayList<ItemHelper> items;
	public ItemMenu(int id)
	{
		super(id);
		items = new ArrayList<ItemHelper>();
	}
	
	public void registerItem(ItemHelper item)
	{
		items.add(item);
	}
	
	public abstract String getTitle();
	public abstract String getDesc();
	public abstract FormMenu getOrigin();
	
	@Override
	public FormWindow createForm(Player player)
	{
		FormWindowSimple menu = new FormWindowSimple(getTitle(), getDesc());
		for(ItemHelper item : items)
		{
			menu.addElement(item);
		}
		menu.addElement(backButton);
		return menu;
	}
	
	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseSimple r = (FormResponseSimple) response;
		if(r == null)
			return;
		
		if(r.getClickedButton().getText().equals(backButton.getText()))
		{
			getOrigin().open(player);
			return;
		} 
		
		if(r.getClickedButton() instanceof ShopItem)
		{
			((ShopItem) r.getClickedButton()).itemResponse(player);
		}
	}
}
