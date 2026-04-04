package com.joshuacc.mrnk.items;

import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.perks.PerkBase;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButtonImageData;

public abstract class PerkItem extends ShopItem 
{
	private final PerkBase perk;
	
	public PerkItem(int index, String text, ElementButtonImageData image, String name, String description, int price,
			int item, int meta, PerkBase perk) {
		super(index, text, image, name, description, price, item, meta, FormsLang.ALLCATEGORY.toString(), false);
		this.perk = perk;
	}
	
	public void addPlayer(Player player)
	{
		perk.addPlayer(player);
	}
	
	public void removePlayer(Player player)
	{
		perk.removePlayer(player);
	}
}