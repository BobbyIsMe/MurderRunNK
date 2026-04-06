package com.joshuacc.mrnk.traps;

import java.util.ArrayList;
import java.util.HashMap;

import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.events.PlayerGlowEvent;
import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.npc.NPCClone;
import com.joshuacc.mrnk.utils.GlowEffect;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;

public class Clones extends TrapDrop
{

	private HashMap<MRTeam, ArrayList<NPCClone>> clones = new HashMap<>();
	
	@Override
	public void performDropAbility(Player player) 
	{
		
	}

	@Override
	protected boolean survivorItem() 
	{
		return true;
	}

	@Override
	protected int getParticle() 
	{
		return Particle.TYPE_BLUE_FLAME;
	}

	@Override
	public String getName() 
	{
		return "Clones";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/clones";
	}

	@Override
	public int getPrice() 
	{
		return 200;
	}

	@Override
	public String getTrapDesc() 
	{
		return "&bClone yourself &faround the map to make the murderer second guess on every survivor encounter!";
	}

	@Override
	public int getItem() 
	{
		return Item.SPAWN_EGG;
	}

	@Override
	public String getTrapName() 
	{
		return "&bClones";
	}
	
	@EventHandler
	public void onSpawnItem(ItemSpawnEvent event)
	{
		EntityItem item = event.getEntity();
		if(item.getItem().getName().equals(getTrapItemName()))
		{
			Player owner = Server.getInstance().getPlayer(item.getItem().getNamedTag().getString("Owner"));
			MRPlayer mPlayer = MRPlayer.getMRPlayer(owner);
			MRTeam team = mPlayer.getMapTeam();
			if(owner != null && mPlayer != null) {
				if(!clones.containsKey(team))
				{
					clones.put(team, new ArrayList<>());
				}
				NPCClone clone = (NPCClone) NPCClone.createEntity("NPCClone", owner.getNameTag(), owner, "", 1F);
				clones.get(team).add(clone);
				item.close();
			}
		}
	}
	
	@EventHandler
	public void onGlow(PlayerGlowEvent event)
	{
		Player player = event.getPlayer();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(mPlayer != null && GlowEffect.getInstance().isGlowing(player))
		{
			for(NPCClone clone : clones.get(mPlayer.getMapTeam()))
			{
				if(clone.getNameTag().equals(player.getNameTag()))
				{
					GlowEffect.getInstance().addGlowingEffect(clone, event.getSeconds(), event.getType());
				}
			}
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event)
	{
		if(event.getDamager() instanceof Player && event.getEntity() instanceof NPCClone)
		{
			Player damager = (Player) event.getDamager();
			MRPlayer mPlayer = MRPlayer.getMRPlayer(damager);
			NPCClone clone = (NPCClone) event.getEntity();
			if(mPlayer != null)
			{
				MRTeam team = mPlayer.getMapTeam();
				if(team.getKiller() == damager)
				{
					clones.get(team).remove(clone);
				}
			}
		}	
	}
	
	@EventHandler
	public void onGameEnd(GameEndEvent event)
	{
		MRTeam team = event.getTeam();
		if(clones.containsKey(team))
		{
			for(NPCClone clone : clones.get(team))
			{
				clone.close();
			}
			
			clones.get(team).clear();
			clones.remove(team);
		}
	}
	
	@EventHandler
	public void onTrigger(TrapTriggeredEvent event)
	{
		if(event.getTrap() == this)
		{
			event.setCancelled(true);
		}
	}
}
