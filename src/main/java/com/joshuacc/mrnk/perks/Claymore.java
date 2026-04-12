package com.joshuacc.mrnk.perks;

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
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.potion.Effect;

public class Claymore extends PerkBase {
	
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
			if(this.containsPlayer(player) && player.hasEffect(Effect.WEAKNESS))
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
			if(PerkDelay.getInstance().hasDelay(this, player))
				PerkDelay.getInstance().removeDelay(this, player);
		}
	}

	@EventHandler
	public void onHit(PlayerToggleSneakEvent event)
	{
		Player player = event.getPlayer();
		if (event.isSneaking())
			simulateAttack(player);
	}
	
	@EventHandler
	public void onPacketReceive(DataPacketReceiveEvent event) {
	    if (!(event.getPacket() instanceof AnimatePacket)) return;

	    AnimatePacket packet = (AnimatePacket) event.getPacket();

	    if (packet.action == AnimatePacket.Action.SWING_ARM) {
	        Player player = event.getPlayer();
	        
	        simulateAttack(player);
	    }
	}
	
	public void simulateAttack(Player player)
	{
		if(this.containsPlayer(player) && !PerkDelay.getInstance().hasDelay(this, player))
		{
			player.getLevel().addSound(player, Sound.MOB_GHAST_FIREBALL, 1F, 1F);
			PerkDelay.getInstance().putDelay(this, player, 1, () -> {
				player.getLevel().addSound(player, Sound.RANDOM_FIZZ, 1F, 1F);
			});
			EntityCreature entity = getEntityInSight(player, 4.2);
			if(entity != null && entity instanceof Player)
			{
				Player target = (Player) entity;
				Item item = player.getInventory().getItemInHand();
				
				if(item == null)
					return;
					
				float damage = item.getAttackDamage();
				
				EntityDamageByEntityEvent e =
					    new EntityDamageByEntityEvent(player, target,
					        EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
				target.attack(e);
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
				if(!PerkDelay.getInstance().hasDelay(this, killer))
				{
					Player target = (Player) event.getEntity();
					PerkDelay.getInstance().putDelay(this, killer, 1, () -> {
						killer.getLevel().addSound(killer, Sound.RANDOM_FIZZ, 1F, 1F);
					});
					target.getLevel().addSound(target, Sound.RANDOM_ANVIL_LAND, 1F, 1F);
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	private EntityCreature getEntityInSight(Player player, double distance)
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
