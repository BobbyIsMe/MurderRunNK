package com.joshuacc.mrnk.traps;

import cn.nukkit.Player;

public abstract class TrapDebuff {
	
	public int[] amplifiers;
	
	public TrapDebuff(int[] amplifiers)
	{
		this.amplifiers = amplifiers;
	}
	
	public abstract void applyDebuff(Player player);
	
	public void setAmplifiers(double percentage)
	{
		for(int i = 0; i < amplifiers.length; i++)
		{
			amplifiers[i] += amplifiers[i] * percentage;
		}
	}
}