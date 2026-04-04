package com.joshuacc.mrnk.main;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.TextFormat;

public abstract class MRPerks implements Comparable<MRPerks>, Listener {

	private static final TreeMap<String, MRPerks> survPerk;
	private static final TreeMap<String, MRPerks> killerPerk;
	private String PerkItemName;

	static {
		survPerk = new TreeMap<String,MRPerks>();
		killerPerk = new TreeMap<String,MRPerks>();
	}	
	
	public String getPerkItemName()
	{
		return PerkItemName;
	}
	
	public void setPerkName()
	{
		PerkItemName = TextFormat.colorize("&r"+MRMain.getInstance().getMRPerksConfig().getString(getName(), "Name"));
	}
	
	public static void addMRPerk(MRPerks Perk, boolean surv, MRMain main)
	{
		Server.getInstance().getPluginManager().registerEvents(Perk, main);
		if(surv)
			survPerk.put(Perk.getName(), Perk);
		else
			killerPerk.put(Perk.getName(), Perk);
	}
	
	public static Collection<MRPerks> getPerks(boolean surv)
	{
		if(surv)
		return Collections.unmodifiableCollection(survPerk.values());
		else
			return Collections.unmodifiableCollection(killerPerk.values());
	}
	
	public static MRPerks getPerk(String name)
	{
		if(survPerk.containsKey(name))
			return survPerk.get(name);
		else
			return killerPerk.get(name);
	}
	
	@Override
	public int compareTo(MRPerks Perks)
	{
		return this.getName().compareTo(Perks.getName());
	}
	
	public abstract String getName();
	public abstract String getIcon();
	public abstract int getPrice();
	public abstract String getPerkDesc();
	public abstract String getPerkName();
}
