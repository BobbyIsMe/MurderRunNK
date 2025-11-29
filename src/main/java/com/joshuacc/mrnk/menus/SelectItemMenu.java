package com.joshuacc.mrnk.menus;

import java.util.HashMap;

import com.joshuacc.mrnk.items.ShopItem;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.TextFormat;

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
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(mPlayer == null)
			return null;
		
		ShopItem item = playerItem.get(player);
		FormWindowSimple menu = new FormWindowSimple(FormsLang.SELITEMNAME.toString(), "");
		ElementButton button = item.getImage() != null ? new ElementButton(FormsLang.SELITEMPURCHASE.toString(), item.getImage()) : new ElementButton(FormsLang.SELITEMPURCHASE.toString());
		menu.addElement(backButton);
		menu.addButton(button);
		menu.addElement(new ElementLabel(TextFormat.colorize(item.getName())));
		menu.addElement(new ElementLabel(
				TextUtils.formatNumber(FormsLang.ITEMPRICE.toString(), item.getPrice())
				+ "\n" + TextUtils.formatNumber(FormsLang.POINTSLEFT.toString(), mPlayer.getPlayerQueuedPoints())));
		menu.addElement(new ElementLabel(TextFormat.colorize(item.getDescription())));
		return menu;
	}

	@Override
	public void response(Player player, FormResponse response) 
	{
		ShopItem item = playerItem.get(player);
		
		if(item == null)
			return;
		
		FormResponseSimple r = (FormResponseSimple) response;
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		
		if(r == null || mPlayer == null)
		{
			playerItem.remove(player);
			return;
		}
		
		if(r.getClickedButton().getText().equals(backButton.getText()))
		{
			item.getCategory().open(player);
			playerItem.remove(player);
		} else {
			if(!player.getInventory().isFull())
				if(!item.isStackable() && player.getInventory().contains(item.getItem()))
				{
					player.getLevel().addSound(player, Sound.MOB_VILLAGER_NO);
					player.sendMessage(TextFormat.colorize(ConfigLang.BUYITEMSTACK.toString().replace("%s", item.getName())));
				}
				else if(mPlayer.getPlayerQueuedPoints() >= item.getPrice())
				{
					Item it = item.getItem();
					if(item.getType().equals(FormsLang.TRAPDROP.toString()))
					{
						it.getNamedTag().putBoolean("Droppable", true);
						it.setNamedTag(it.getNamedTag());
					}
					player.getInventory().addItem(item.getItem());
					player.getLevel().addSound(player, Sound.RANDOM_ORB);
					mPlayer.addPoints(-item.getPrice());
					player.sendMessage(TextUtils.format(ConfigLang.BUYITEMSUCCESS.toString().replace("%s", item.getName()).replace("%n", Integer.toString(item.getPrice()))));
				} else
				{
					player.getLevel().addSound(player, Sound.RANDOM_ANVIL_LAND);
					player.sendMessage(TextUtils.format(ConfigLang.BUYITEMFAIL.toString().replace("%s", item.getName()).replace("%n", Integer.toString(item.getPrice() - mPlayer.getPlayerQueuedPoints()))));
				}
			this.open(player);
		}
	}
}
