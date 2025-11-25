package com.joshuacc.mrnk.main;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;

public abstract class MRTraps implements Comparable<MRTraps>, Listener {

	private static final TreeMap<String, MRTraps> survTrap;
	private static final TreeMap<String, MRTraps> killerTrap;

	static {
		survTrap = new TreeMap<String,MRTraps>();
		killerTrap = new TreeMap<String,MRTraps>();
	}	
	
	public static void addMRTrap(MRTraps trap, boolean surv)
	{
		if(surv)
			survTrap.put(trap.getName(), trap);
		else
			killerTrap.put(trap.getName(), trap);
	}
	
	public static Collection<MRTraps> getTraps(boolean surv)
	{
		if(surv)
		return Collections.unmodifiableCollection(survTrap.values());
		else
			return Collections.unmodifiableCollection(killerTrap.values());
	}
	
	public static MRTraps getTrap(String name)
	{
		if(survTrap.containsKey(name))
			return survTrap.get(name);
		else
			return killerTrap.get(name);
	}
	
	@Override
	public int compareTo(MRTraps traps)
	{
		return this.getName().compareTo(traps.getName());
	}
	
	public abstract String getName();
	public abstract String getTrapDesc();
	public abstract boolean oneTimeUse();
	public abstract int getCooldown();
	public abstract void getAbility(Player player);
	protected abstract Item getItem();
	protected abstract String getTrapName();
	protected abstract String getLore();
	
	public Item addItem()
	{
		Item item = getItem();
		item.setCustomName(getName());
		item.setLore(getLore());
		return item;
	}
}
