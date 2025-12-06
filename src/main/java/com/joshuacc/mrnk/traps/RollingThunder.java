package com.joshuacc.mrnk.traps;

import java.util.ArrayList;
import java.util.HashMap;

import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.utils.GameScheduler.Schedulers;
import com.joshuacc.mrnk.utils.GameTask;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;

public class RollingThunder extends TrapClick {

	private HashMap<String,ArrayList<Block>> blocks;
	private String message1;
	private String message2;
	
	public RollingThunder()
	{
		blocks = new HashMap<>();
		MRTrapsConfig traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message.1", "&e&lROLLING...");
		traps.putDefault(getName(), "Message.2", "&e&lTHUNDAA!");
		message1 = TextFormat.colorize(traps.getString(getName(), "Message.1"));
		message2 = TextFormat.colorize(traps.getString(getName(), "Message.2"));
	}
	
	@Override
	public int getDelay() 
	{
		return 180;
	}

	@Override
	protected boolean performClickAbility(Player player) 
	{
		blocks.putIfAbsent(player.getName(), new ArrayList<>());
		player.sendTitle(message1, "", 10, 40, 10);
		player.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(20 * 3));
		for(Entity e : player.getLevel().getNearbyEntities(player.boundingBox.grow(8, 8, 8)))	
		{
			if(e instanceof Player)
			{
				Player p = (Player) e;
				p.sendTitle(message1, "", 10, 40, 10);
			}
		}
		GameTask task = new GameTask(120);
		task.addLoopTask(() -> {
			if(!player.isOnline())
			{
				task.cancel();
				MRPlayer.getMRPlayer(player).removeGameTask(task);
				return;
			}
			
			if(task.getDelay() == 60)
			{
				player.sendTitle(message2, "", 10, 40, 10);
				for(Entity e : player.getLevel().getNearbyEntities(player.boundingBox.grow(8, 8, 8)))	
				{
					if(e instanceof Player)
					{
						Player p = (Player) e;
						p.sendTitle(message2, "", 10, 40, 10);
					}
				}
			}
			
			if(task.getDelay() > 60)
			{
				player.getLevel().addSound(player, Sound.NOTE_PLING);
			} else {
				Explosion explode = new Explosion(player, 3, player);
				explode.explodeA();
				explode.explodeB();
				player.getLevel().addParticle(new HugeExplodeSeedParticle(player));
				player.getLevel().addSound(player, Sound.RANDOM_EXPLODE);
				player.setMotion(player.getDirectionVector().multiply(1));
				for(Entity e : player.getLevel().getNearbyEntities(player.boundingBox.grow(3, 3, 3)))	
				{
					if(e instanceof Player)
					{
						Player p = (Player) e;
						if(p.getName() != player.getName())
							p.attack(4);
					}
				}
			}
		});
		task.addEndTask(() -> {
			for(Block block : blocks.get(player.getName()))
			{
				block.getLevel().setBlock(block, block);
			}
			blocks.remove(player.getName());
			if(!task.isCancelled())
				MRPlayer.getMRPlayer(player).removeGameTask(task);
		});
		MRPlayer.getMRPlayer(player).addGameTask(task, Schedulers.TICK1);
		return true;
	}

	@EventHandler
	public void onExplodePlayer(EntityDamageByEntityEvent event)
	{
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player)
		{
			if(event.getCause() == DamageCause.ENTITY_EXPLOSION)
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onUpdate(BlockUpdateEvent event) throws Exception
	{
		if(event.getBlock().hasMetadata("Player"))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onSuffocate(EntityDamageEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			if(event.getCause() == DamageCause.SUFFOCATION)
			{
				Player player = (Player) event.getEntity();
				Location loc = player.getLocation().clone();
				MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
				if(mPlayer != null && player.getLevel().equals(mPlayer.getMapTeam().getMapLevel()))
				{
					int y = (int) player.getY();
					for(int i = y; i < (y+50); i++)
					{
						loc.setY(i);
						if(loc.getLevelBlock().getItemId() == Block.AIR)
							break;
					}
					
					player.teleport(loc);
				}
			}
		}
	}
	
	@EventHandler
	public void onExplodeBlocks(EntityExplodeEvent event) throws Exception
	{
		if(event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			String name = player.getName();
			MetadataValue metadata = new MetadataValue(MRMain.getInstance()) {
			    @Override
			    public Object value() {
			        return name;
			    }

			    @Override
			    public void invalidate() {}
			};
			if(blocks.containsKey(name))
			{
				for(Block block : event.getBlockList())
				{
					if(block.getItemId() == Block.AIR)
						continue;
					
					block.setMetadata("Player", metadata);
					Block b = block.clone();
					blocks.get(name).add(b);
					block.getLevel().setBlock(block, Block.get(Block.AIR));
				}
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onTrapTrigger(TrapTriggeredEvent event)
	{
		if(blocks.containsKey(event.getOwner().getName()))
			event.setCancelled(true);
	}
	
	@Override
	public String getName() 
	{
		return "RollingThunder";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/rolling_thunder";
	}

	@Override
	public int getPrice() 
	{
		return 400;
	}

	@Override
	public String getTrapDesc() 
	{
		return "&bBulldoze &feverything that is blocking your path for &e3 seconds!\n\n&7Cooldown: &a3 minutes";
	}

	@Override
	public int getItem() 
	{
		return Item.BLAZE_ROD;
	}

	@Override
	public String getTrapName() 
	{
		return "&e&lRolling Thunder";
	}

}
