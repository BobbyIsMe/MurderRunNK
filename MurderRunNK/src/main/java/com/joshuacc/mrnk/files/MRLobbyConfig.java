package com.joshuacc.mrnk.files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.scheduler.Task;

public class MRLobbyConfig extends AbstractFiles {

	private MRMain main;
	private ArrayList<FloatingTextParticle> holograms = new ArrayList<>();
	private HashMap<FloatingTextParticle,FloatingTextParticle> modes = new HashMap<>();

	public MRLobbyConfig(MRMain main) {
		super(main, "MRLobbyConfig");
		this.main = main;

		for(MapModes mode : MapModes.values())
			addNPCTexts(mode);
	}

	@Override
	public void addDefaults() 
	{
		for(MapModes mode : MapModes.values())
			addDefault(mode.getMode()+"-id", 0);
		config.save();
	}

	public void setupLobbyLocation(Player player, boolean l)
	{
		String lobby = "Lobby Level";
		if(!l)
			lobby = "W-"+lobby;
		config.set(lobby+".Name", player.getLevel().getName());
		config.set(lobby+".X", player.getX());
		config.set(lobby+".Y", player.getY());
		config.set(lobby+".Z", player.getZ());
		config.set(lobby+".Yaw", player.getYaw());
		config.set(lobby+".Pitch", player.getPitch());
		config.save();
	}

	public void addJoinNPCDetails(MapModes mode, Entity ent)
	{
		String type = mode.getMode();
		int id = config.getInt(type+"-id")+1;
		String prefix = "NPC."+type+".";
		config.set(prefix+id+".X", ent.getX());
		config.set(prefix+id+".Y", ent.getY()+(ent.getHeight()+ent.getScale())+1.05);
		config.set(prefix+id+".Z", ent.getZ());
		config.set(prefix+id+".Level", ent.getLevel().getName());
		config.set(type+"-id", id);
		config.save();
		ent.namedTag.putInt("npc-tag", config.getInt(type+"-id"));
		ent.namedTag.putString("npc-type", type);
		playerScheduler(getHologramLocation(type, id), id, mode);
	}
	
	public void addJoinNPCDetails(String type, Entity ent)
	{
		int id = config.getInt(type+"-id")+1;
		String prefix = "NPC."+type+".";
		config.set(prefix+id+".X", ent.getX());
		config.set(prefix+id+".Y", ent.getY()+(ent.getHeight()+ent.getScale())+1.05);
		config.set(prefix+id+".Z", ent.getZ());
		config.set(prefix+id+".Level", ent.getLevel().getName());
		config.set(type+"-id", id);
		config.save();
		ent.namedTag.putInt("npc-tag", config.getInt(type+"-id"));
		ent.namedTag.putString("npc-type", type);
	}

	public Location getMainLobbyLocation()
	{
		return new Location(
				getLobbyValue("X"), 
				getLobbyValue("Y"), 
				getLobbyValue("Z"), 
				getLobbyValue("Yaw"), 
				getLobbyValue("Pitch"),
				Server.getInstance().getLevelByName(config.getString("Lobby Level.Name")));
	}

	public Location getQueueLobbyLocation()
	{
		return new Location(
				getWLobbyValue("X"), 
				getWLobbyValue("Y"), 
				getWLobbyValue("Z"), 
				getWLobbyValue("Yaw"), 
				getWLobbyValue("Pitch"),
				Server.getInstance().getLevelByName(config.getString("W-Lobby Level.Name")));
	}

	public void playerScheduler(Location vec, int i, MapModes m)
	{
		String type = m.getMode();
		if(config.get("NPC."+type+"."+i) == null)
			return;

		Level level = vec.getLevel();
		FloatingTextParticle particle = new FloatingTextParticle(vec, "0");
		FloatingTextParticle mode = new FloatingTextParticle(vec.add(0, 0.5, 0), "");

		mode.setTitle(m.getHologram());
		level.addParticle(particle);
		level.addParticle(mode);

		holograms.add(particle);
		modes.put(particle, mode);

		Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() {

			@Override
			public void onRun(int arg0) 
			{	
				int players = main.getPlayerCount(m);

				particle.setTitle(TextUtils.formatNumber(ConfigLang.NPCJOINPLAYERS.toString(), players));

				if(config.get("NPC."+type+"."+i) == null)
				{
					particle.setTitle("");
					modes.get(particle).setTitle("");
					modes.get(particle).setInvisible(true);;
					particle.setInvisible(true);
					holograms.remove(particle);
					this.cancel();
				}
			}

		}, 20 * 5);
	}

	public ArrayList<FloatingTextParticle> getHolograms()
	{
		return holograms;
	}

	public Collection<FloatingTextParticle> getModesHologram()
	{
		return modes.values();
	}

	public void removePlayer(Entity ent)
	{
		config.set("NPC."+ent.namedTag.getString("npc-type")+"."+ent.namedTag.getString("npc-tag"), null);
		config.save();
		ent.close();
	}

	private void addNPCTexts(MapModes mode)
	{
		String type = mode.getMode();
		for(int i = 1; i <= config.getDouble(type+"-id"); i++)
			playerScheduler(getHologramLocation(type, i), i, mode);
	}

	private Location getHologramLocation(String type, int i)
	{
		return new Location(
				getNPCValue(type, i, "X"),
				getNPCValue(type, i, "Y"),
				getNPCValue(type, i, "Z"), 0, 0, Server.getInstance().getLevelByName(getNPCLocation(type, i, "Level")));
	}

	private String getNPCLocation(String type, int id, String child)
	{
		return config.getString("NPC."+type+"."+id+"."+child);
	}

	private Double getNPCValue(String type, int id, String child)
	{
		return config.getDouble("NPC."+type+"."+id+"."+child);
	}

	private Double getLobbyValue(String child)
	{
		return config.getDouble("Lobby Level."+child);
	}

	private Double getWLobbyValue(String child)
	{
		return config.getDouble("W-Lobby Level."+child);
	}
}
