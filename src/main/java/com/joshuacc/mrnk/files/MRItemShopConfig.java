package com.joshuacc.mrnk.files;

import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.item.Item;
import cn.nukkit.utils.ConfigSection;

public class MRItemShopConfig extends AbstractFiles {

	public MRItemShopConfig(MRMain main) 
	{
		super(main, "MRItemShopConfig");
	}

	@Override
	public void addDefaults() 
	{
		String armor = "Armor";
		String path = "path";
		registerItemConfig(armor, "Chainmail Helmet", path, "textures/items/chainmail_helmet", "&eChainmail Helmet", "", 50, Item.CHAINMAIL_HELMET);
		registerItemConfig(armor, "Chainmail Chestplate", path, "textures/items/chainmail_chestplate", "&eChainmail Chestplate", "", 50, Item.CHAINMAIL_CHESTPLATE);
		registerItemConfig(armor, "Chainmail Leggings", path, "textures/items/chainmail_leggings", "&eChainmail Leggings", "", 50, Item.CHAINMAIL_LEGGINGS);
		registerItemConfig(armor, "Chainmail Boots", path, "textures/items/chainmail_boots", "&eChainmail Boots", "", 50, Item.CHAINMAIL_BOOTS);
	}

	public void registerItemConfig(String type, String key, String path, String image, String name, String description, int price, int material)
	{
		String item = new StringBuilder(type).append(".").append(key).append(".").toString();
		addDefault(item+"Path", path);
		addDefault(item+"Image", image);
		addDefault(item+"Name", name);
		addDefault(item+"Description", description);
		addDefault(item+"Price", price);
		addDefault(item+"Item", material);
	}
	
	public ConfigSection getAllItemsByType(String type)
	{
		return config.getSection(type);
	}
}
