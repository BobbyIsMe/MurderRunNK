package com.joshuacc.mrnk.main;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.TextFormat;

public abstract class MRTraps implements Comparable<MRTraps>, Listener {

	private static final TreeMap<String, MRTraps> survTrap;
	private static final TreeMap<String, MRTraps> killerTrap;
	private String trapItemName;

	static {
		survTrap = new TreeMap<String,MRTraps>();
		killerTrap = new TreeMap<String,MRTraps>();
	}	
	
	public String getTrapItemName()
	{
		return trapItemName;
	}
	
	public void setTrapName()
	{
		trapItemName = TextFormat.colorize("&r"+MRMain.getInstance().getMRTrapsConfig().getString(getName(), "Name"));
	}
	
	public static void addMRTrap(MRTraps trap, boolean surv, MRMain main)
	{
		Server.getInstance().getPluginManager().registerEvents(trap, main);
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
	
	public int getMeta()
	{
		return 1;
	}
	
	@Override
	public int compareTo(MRTraps traps)
	{
		return this.getName().compareTo(traps.getName());
	}
	
	public abstract String getName();
	public abstract String getIcon();
	public abstract int getPrice();
	public abstract String getTrapDesc();
	public abstract boolean oneTimeUse();
	public abstract int getItem();
	public abstract String getType();
	public abstract boolean isStackable();
	public abstract String getTrapName();
}
