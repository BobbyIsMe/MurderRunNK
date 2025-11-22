package com.joshuacc.mrnk.items;

import java.util.HashMap;

import com.joshuacc.mrnk.lang.FormsLang;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public class SelectItemMenu extends FormMenu {

	private HashMap<Player,ShopItem> playerItem;
	
	public SelectItemMenu(int id) {
		super(id);
		playerItem = new HashMap<>();
	}
	
	public void addItemToPlayer(Player player, ShopItem item)
	{
		playerItem.put(player, item);
	}
	
	@Override
	public FormWindow createForm(Player player) 
	{
		ShopItem item = playerItem.get(player);
		FormWindowSimple menu = new FormWindowSimple(FormsLang.SELITEMNAME.toString(), "");
		ElementButton button = item.getImage() != null ? new ElementButton(FormsLang.SELITEMPURCHASE.toString(), item.getImage()) : new ElementButton(FormsLang.SELITEMPURCHASE.toString());
		menu.addElement(new ElementLabel(item.getName()));
		menu.addElement(new ElementLabel(item.getDescription()));
		menu.addElement(new ElementLabel(Integer.toString(item.getPrice())));
		menu.addElement(backButton);
		menu.addButton(button);
		return menu;
	}

	@Override
	public void response(Player player, FormResponse response) 
	{
		ShopItem item = playerItem.get(player);
		
		if(item == null)
			return;
		
		FormResponseSimple r = (FormResponseSimple) response;
		
		if(r == null)
		{
			player.sendMessage("closed");
			playerItem.remove(player);
			return;
		}
		
		if(r.getClickedButton().getText().equals(backButton.getText()))
		{
			item.getCategory().open(player);
			playerItem.remove(player);
		} else {
			//TODO: setup shop stuff
			player.getInventory().addItem(item.getItem());
			this.open(player);
		}
	}

}
