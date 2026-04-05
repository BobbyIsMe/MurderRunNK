package com.joshuacc.mrnk.perks;

import java.util.HashMap;
import java.util.UUID;

import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;

public class Claymore extends PerkBase {

	private HashMap<UUID, Long> hitDelay = new HashMap<>();
	
	@Override
	public String getName() 
	{
		return "Claymore";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/claymore";
	}

	@Override
	public int getPrice() 
	{
		return 500;
	}

	@Override
	public String getPerkDesc() 
	{
		return "Increase your range by &b5 blocks&f while also &bdealing more damage&f. But holding the claymore will give you &bslowness.\n\n&7Cooldown: &a1 second";
	}

	@Override
	public String getPerkName() 
	{
		return "&l&fClaymore";
	}
	
	@EventHandler
	public void onGameStart(GameStartEvent event)
	{
		if(event.getGameAttribute() == GameAttribute.ROUND)
		{
			Player killer = event.getTeam().getKiller();
			if(killer == null)
				return;
			
			killer.addEffect(Effect.getEffect(Effect.STRENGTH).setDuration(Integer.MAX_VALUE).setAmplifier(0));
			killer.addEffect(Effect.getEffect(Effect.MINING_FATIGUE).setDuration(Integer.MAX_VALUE).setAmplifier(2));
			killer.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(Integer.MAX_VALUE).setAmplifier(1));
		}
	}
	
	@EventHandler
	public void onEffect(EntityPotionEffectEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			if(this.containsPlayer(player) && event.getNewEffect().getId() == Effect.WEAKNESS)
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onGameEnd(GameEndEvent event)
	{
		for(Player player : event.getTeam().getPlayers())
		{
			if(hitDelay.containsKey(player.getUniqueId()))
				hitDelay.remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void onHit(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if(event.getAction() == Action.LEFT_CLICK_AIR)
		{
			if(this.containsPlayer(player) && !hasHitDelay(player))
			{
				hitDelay.put(player.getUniqueId(), System.currentTimeMillis()+1000);
				EntityCreature entity = getEntityInSight(player, 5);
				if(entity instanceof Player)
				{
					Player target = (Player) entity;
					Item item = player.getInventory().getItemInHand();
					float damage = item.getAttackDamage();
					
					EntityDamageByEntityEvent e =
						    new EntityDamageByEntityEvent(player, target,
						        EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
					target.attack(e);
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event)
	{
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player)
		{
			Player killer = (Player) event.getDamager();
			if(this.containsPlayer(killer))
			{
				if(!hasHitDelay(killer))
				{
					Player target = (Player) event.getEntity();
					hitDelay.put(killer.getUniqueId(), System.currentTimeMillis()+1000);
					target.getLevel().addSound(killer, Sound.RANDOM_ANVIL_LAND, 1F, 2F, target);
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	public boolean hasHitDelay(Player player)
	{
		long delay = hitDelay.get(player.getUniqueId());
		return hitDelay.containsKey(player.getUniqueId()) && System.currentTimeMillis() < delay;
	}
	
	private EntityCreature getEntityInSight(Player player, int distance)
	{
		EntityCreature inSight = null;
		for(Entity nearbyEntity : player.getLevel().getNearbyEntities(player.getBoundingBox().grow(distance, distance, distance), player))
		{
			if(nearbyEntity instanceof EntityCreature)
			{
				if(nearbyEntity.boundingBox.isVectorInside(distance(player, nearbyEntity.distance(player))))
				{
					inSight = (EntityCreature) nearbyEntity;
					return inSight;
				}
			}
		}
		return inSight;
	}
	
	private Vector3 distance(Player player, double blocksAway) {
		return player.add(0, player.getEyeHeight(), 0).add(player.getDirectionVector().multiply(blocksAway));
	}
}
