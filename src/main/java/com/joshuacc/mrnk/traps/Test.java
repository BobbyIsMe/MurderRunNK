package com.joshuacc.mrnk.traps;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class Test extends TrapClick {

	@Override
	public int getDelay() 
	{
		return 10;
	}

	@Override
	protected boolean performClickAbility(Player player) 
	{
		player.sendMessage("test");
		return true;
	}

	@Override
	public Item getItem() 
	{
		return new Item(Item.ICE);
	}

	@Override
	public String getTrapName() 
	{
		return "Test";
	}

	@Override
	public String getIcon() 
	{
		return "textures/blocks/blue_ice";
	}

	@Override
	public int getPrice() 
	{
		return 100;
	}

	@Override
	public String getName() 
	{
		return "Test";
	}

	@Override
	public String getTrapDesc() 
	{
		return "I am testing it";
	}
}
