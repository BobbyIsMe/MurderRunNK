package com.joshuacc.mrnk.items;

import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;

public abstract class TrapItem  extends ShopItem {

	private final String trapName;
	
	public TrapItem(int index, String text, ElementButtonImageData image, String name, String description, int price,
			int item, int meta, String category, boolean stackable, String trapName) {
		super(index, text, image, name, description, price, item, meta, category, stackable);
		this.trapName = trapName;
	}
	
	@Override
	public Item getItem()
	{
		Item i = Item.get(item, meta);
		i.setCustomName(TextFormat.colorize("&r"+name));
		if(description.length() != 0)
			i.setLore(TextFormat.colorize("&r"+description));
		i.getNamedTag().putBoolean("Droppable", false);
		i.getNamedTag().putString("Trap", trapName);
		i.setNamedTag(i.getNamedTag());
		return i;
	}

}
