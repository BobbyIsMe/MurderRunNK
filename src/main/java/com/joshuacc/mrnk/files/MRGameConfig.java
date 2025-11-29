package com.joshuacc.mrnk.files;

import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBootsLeather;
import cn.nukkit.item.ItemChestplateLeather;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.item.ItemHelmetLeather;
import cn.nukkit.item.ItemLeggingsLeather;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;

public class MRGameConfig extends AbstractFiles {

	public MRGameConfig(MRMain main) 
	{
		super(main, "MRGameConfig");
	}

	@Override
	public void addDefaults() 
	{
		addDefault("Color.Red", 0);
		addDefault("Color.Blue", 0);
		addDefault("Color.Green", 0);
		addDefault("Sword.Name", "&4&lDangerous Dagger");
		addDefault("Sword.Material", Item.NETHERITE_SWORD);
		addDefault("Sword.Sharpness", 2);
		addDefault("Points.Kill", 60);
		addDefault("Points.Round", 80);
		addDefault("Points.Minute", 30);
		addDefault("Points.Survive", 500);
		addDefault("Points.Escape", 300);
		addDefault("Points.Win-1", 600);
		addDefault("Points.Win-2", 400);
		addDefault("Points.Win-3", 200);
		addDefault("Points.Win", 100);
	}
	
	public void giveItem(Player player)
	{
		int r = config.getInt("Color.Red");
		int g = config.getInt("Color.Green");
		int b = config.getInt("Color.Blue");
		ItemColorArmor helmet = new ItemHelmetLeather().setColor(r, g, b);
		ItemColorArmor chestplate = new ItemChestplateLeather().setColor(r, g, b);
		ItemColorArmor leggings = new ItemLeggingsLeather().setColor(r, g, b);
		ItemColorArmor boots = new ItemBootsLeather().setColor(r, g, b);
		Item item = Item.get(config.getInt("Sword.Material"));
		item.addEnchantment(Enchantment.get(Enchantment.ID_DAMAGE_ALL).setLevel(config.getInt("Sword.Sharpness")));
		item.addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY).setLevel(10));
		item.setCustomName(TextFormat.colorize(config.getString("Sword.Name")));
		
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
		player.getInventory().addItem(item);
		
		for(Item i : player.getInventory().getContents().values()) 
		{
		    if (i == null || i.isNull()) 
		    	continue;
		    
		    i.getNamedTag().putBoolean("Droppable", false);
			i.setNamedTag(i.getNamedTag());
		}
		
		
		for(Item i : player.getInventory().getArmorContents()) 
		{
		    if (i == null || i.isNull()) 
		    	continue;
		    
		    i.getNamedTag().putBoolean("Droppable", false);
			i.setNamedTag(i.getNamedTag());
		}
	}

	public int getKillPoints()
	{
		return config.getInt("Points.Kill");
	}

	public int getRoundPoints()
	{
		return config.getInt("Points.Round");
	}

	public int getMinutePoints()
	{
		return config.getInt("Points.Minute");
	}

	public int getSurvivePoints()
	{
		return config.getInt("Points.Survive");
	}

	public int getEscapePoints()
	{
		return config.getInt("Points.Escape");
	}

	public int getWinPoints()
	{
		return config.getInt("Points.Win");
	}

	public int getWinPoints(int position)
	{
		return config.getInt("Points.Win-"+position);
	}
}
