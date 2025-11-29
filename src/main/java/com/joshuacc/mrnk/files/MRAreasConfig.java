package com.joshuacc.mrnk.files;

import java.util.HashMap;

import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;

public class MRAreasConfig extends AbstractFiles {

	private HashMap<Player, Area> playerAreas;
	
	public MRAreasConfig(MRMain main) 
	{
		super(main, "MRAreasConfig");
		this.playerAreas = new HashMap<>();
	}

	@Override
	public void addDefaults() 
	{
		addDefault("Area-Item Name", "&eArea Wand");
	}
	
	public String getAreaItemName()
	{
		return config.getString("Area-Item Name");
	}
	
	public void addPos1(Player player, Location loc)
	{
		playerAreas.putIfAbsent(player, new Area());
		playerAreas.get(player).setPos1(loc);
	}
	
	public void addPos2(Player player, Location loc)
	{
		playerAreas.putIfAbsent(player, new Area());
		playerAreas.get(player).setPos2(loc);
	}
	
	private Vector3 getPos1(Player player)
	{
		Area area = playerAreas.get(player);
		return area != null ? area.getPos1() : null;
	}
	
	private Vector3 getPos2(Player player)
	{
		Area area = playerAreas.get(player);
		return area != null ? area.getPos2() : null;
	}
	
	public void giveAreaItem(Player player)
	{
		Item item = Item.get(Item.STICK);
		item.setCustomName(TextFormat.colorize(getAreaItemName()));
		player.getInventory().addItem(item);
	}
	
	public void addArea(Player player, String hunger, String damage, String blocks)
	{
		Vector3 pos1 = getPos1(player);
		Vector3 pos2 = getPos2(player);
		String level = player.getLevel().getName();
		if(pos1 == null)
		{
			player.sendMessage(ConfigLang.AREAADDEDNOPOS1.toString());
			return;
		}
		
		if(pos2 == null)
		{
			player.sendMessage(ConfigLang.AREAADDEDNOPOS2.toString());
			return;
		}
		boolean h = hunger.equals("true") ? true : false;
		boolean d = damage.equals("true")? true : false;
		boolean b = blocks.equals("true") ? true : false;
		ConfigSection section = config.getSection(level);
	    int max = 0;

	    for (String key : section.getKeys(false)) {
	        try {
	            int num = Integer.parseInt(key);
	            if (num > max) max = num;
	        } catch (Exception ex) {
	        	
	        }
	    }
	    
	    String key = new StringBuilder(level).append(".").append(max + 1).append(".").toString();
	    addDefault(key+"Loc1.X", pos1.x);
	    addDefault(key+"Loc1.Y", pos1.y);
	    addDefault(key+"Loc1.Z", pos1.z);
	    addDefault(key+"Loc2.X", pos2.x);
	    addDefault(key+"Loc2.Y", pos2.y);
	    addDefault(key+"Loc2.Z", pos2.z);
	    addDefault(key+"Hunger", h);
	    addDefault(key+"Damage", d);
	    addDefault(key+"Break Blocks", b);
	    config.save();
	    playerAreas.remove(player);
	    player.sendMessage(TextUtils.format(ConfigLang.AREAADDEDSUCCESS.toString().replace("%l", level)));
	}
	
	public ConfigSection getArea(Player player)
	{
		String level = player.getLevel().getName();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(mPlayer != null && player.getLevel().equals(mPlayer.getMapTeam().getMapLevel()))
			level = mPlayer.getMapTeam().getMapLevelOriginName();
		
		ConfigSection section = config.getSection(level);
	    if (section == null) return null;

	    for (String key : section.getKeys(false)) 
	    {
	        ConfigSection area = section.getSection(key);
	        if (area == null) continue;

	        Vector3 pos1 = new Vector3(area.getDouble("Loc1.X"), area.getDouble("Loc1.Y"), area.getDouble("Loc1.Z"));
	        Vector3 pos2 = new Vector3(area.getDouble("Loc2.X"), area.getDouble("Loc2.Y"), area.getDouble("Loc2.Z"));
	        
	        if (isInside(pos1, pos2, player))
	            return area;
	    }
	    return null;
	}
	
	public void removeArea(Player player)
	{
		String level = player.getLevel().getName();
		ConfigSection section = config.getSection(level);
		boolean removed = false;
	    if (section == null) return;

	    for (String key : section.getKeys(false)) 
	    {
	        ConfigSection area = section.getSection(key);
	        if (area == null) continue;

	        Vector3 pos1 = new Vector3(area.getDouble("Loc1.X"), area.getDouble("Loc1.Y"), area.getDouble("Loc1.Z"));
	        Vector3 pos2 = new Vector3(area.getDouble("Loc2.X"), area.getDouble("Loc2.Y"), area.getDouble("Loc2.Z"));
	        
	        if (isInside(pos1, pos2, player))
	        {
	            section.remove(key);
	            config.save();
	            removed = true;
	            break;
	        }
	    }
	    
	    player.sendMessage(removed ? TextUtils.format(ConfigLang.AREAREMOVEDSUCCESS.toString().replace("%l", level)) : TextUtils.format(ConfigLang.AREAREMOVEDFAILED.toString()));
	}
	
	private boolean isInside(Vector3 pos1, Vector3 pos2, Player player) {
	    double x = player.getX();
	    double y = player.getY();
	    double z = player.getZ();

	    double minX = Math.min(pos1.x, pos2.x);
	    double maxX = Math.max(pos1.x, pos2.x);

	    double minY = Math.min(pos1.y, pos2.y);
	    double maxY = Math.max(pos1.y, pos2.y);

	    double minZ = Math.min(pos1.z, pos2.z);
	    double maxZ = Math.max(pos1.z, pos2.z);

	    return (x >= minX && x <= maxX) &&
	           (y >= minY && y <= maxY) &&
	           (z >= minZ && z <= maxZ);
	}
}

class Area {
	private Location pos1;
	private Location pos2;
	
	public Area()
	{
		this.pos1 = null;
		this.pos2 = null;
	}
	
	public void setPos1(Location pos)
	{
		pos1 = pos;
	}
	
	public void setPos2(Location pos)
	{
		pos2 = pos;
	}
	
	public Vector3 getPos1()
	{
		return pos1;
	}
	
	public Vector3 getPos2()
	{
		return pos2;
	}
}
