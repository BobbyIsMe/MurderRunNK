package com.joshuacc.mrnk.menus;

import java.util.Map.Entry;

import com.joshuacc.mrnk.items.PerkItem;
import com.joshuacc.mrnk.items.ShopItem;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;

public class SellItemsMenu extends FormMenu 
{

	public SellItemsMenu(int id) {
		super(id);
	}

	@Override
	public FormWindow createForm(Player player) 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		FormWindowSimple menu = new FormWindowSimple(FormsLang.SELLITEMSTITLE.toString(), FormsLang.SELLITEMSDESC.toString());
		if(mPlayer != null)
		{
			for(ShopItem item : mPlayer.getShopItems())
			{
				menu.addButton(item);
			}
		}
		return menu;
	}

	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseSimple r = (FormResponseSimple) response;
		if(r == null)
			return;
		
		ElementButton button = r.getClickedButton();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		
		if(button instanceof ShopItem)
		{
			ShopItem shopItem = (ShopItem) button;
			int price = shopItem.getPrice();
			
			player.sendMessage(TextUtils.format(ConfigLang.SELLITEMSUCESS.toString().replace("%s", shopItem.getName()).replace("%n", Integer.toString(price))));
			for(Entry<Integer, Item> entry : player.getInventory().getContents().entrySet()) 
			{
		        int slot = entry.getKey();
		        Item item = entry.getValue();
		        
		        if(item != null && item.getId() != 0 && item.getName().contains(shopItem.getName())) 
				{
					player.getInventory().decreaseCount(slot);
					break;
				}
			}
			
			mPlayer.removeShopItem(shopItem);
			mPlayer.addPoints(price);
			
			if(button instanceof PerkItem)
			{
				((PerkItem) shopItem).removePlayer(player);
			}
		}
	}

}
