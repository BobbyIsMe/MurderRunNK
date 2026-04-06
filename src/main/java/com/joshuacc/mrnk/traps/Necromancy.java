package com.joshuacc.mrnk.traps;

import java.util.HashMap;

import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.npc.NPCCorpse;
import com.joshuacc.mrnk.utils.GameTask;
import com.joshuacc.mrnk.utils.TaskAct;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;

public class Necromancy extends TrapDrop {
	
	private String message;
	private String tag;
	private HashMap<MRTeam, Player> zombies;
	
	public Necromancy()
	{
		zombies = new HashMap<>();
		MRTrapsConfig traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message", "&4&l%s1 &chas corrupted &4&l%s2!");
		traps.putDefault(getName(), "Tag", "&c[ZOMBIE] %p");
		message = TextFormat.colorize(traps.getString(getName(), "Message"));
		tag = TextFormat.colorize(traps.getString(getName(), "Tag"));
	}
	
	@Override
	public void performDropAbility(Player player) 
	{
		
	}
	
	@Override
	public boolean isStackable()
	{
		return false;
	}

	@Override
	protected boolean survivorItem() 
	{
		return false;
	}

	@Override
	protected int getParticle() 
	{
		return Particle.TYPE_DUST;
	}

	@Override
	public String getName() 
	{
		return "Necromancy";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/necromancy";
	}

	@Override
	public int getPrice() 
	{
		return 500;
	}

	@Override
	public String getTrapDesc() 
	{
		return "&bCorrupt a player and make them fight for you&f by &bdropping&f this item nearby other corpses!";
	}

	@Override
	public int getItem() 
	{
		return Item.SKULL;
	}

	@Override
	public String getTrapName() 
	{
		return "&4&lNecromancy";
	}
	
	public void addZombie(Player player)
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(mPlayer != null)
		{
			player.setNameTag(tag.replace("%p", player.getName()));
			zombies.put(mPlayer.getMapTeam(), player);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
			if(mPlayer != null && zombies.get(mPlayer.getMapTeam()) == player)
			{
				event.setDamage(0);
			}
		}
	}
	
	@EventHandler
	public void onGameEnd(GameEndEvent event)
	{
		if(zombies.get(event.getTeam()) != null)
		{
			zombies.remove(event.getTeam());
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player); 
		if(mPlayer != null && zombies.get(mPlayer.getMapTeam()) == player)
		{
			zombies.remove(mPlayer.getMapTeam());
		}
	}

	@EventHandler
	public void onSpawnItem(ItemSpawnEvent event)
	{
		EntityItem item = event.getEntity();
		if(item.getItem().getName().equals(getTrapItemName()))
		{
			Player player = Server.getInstance().getPlayer(item.getItem().getNamedTag().getString("Owner"));
			MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
			MRTeam team = mPlayer.getMapTeam();
			if(player != null && mPlayer != null) {
				GameTask task = new GameTask(-1);
				task.addLoopTask(new TaskAct() 
				{

					@Override
					public void doTask() 
					{
						for(Entity ent : item.getLevel().getNearbyEntities(item.getBoundingBox().grow(0.5, 0.5, 0.5))) 
						{
							if(ent instanceof NPCCorpse) 
							{
								NPCCorpse corpse = (NPCCorpse) ent;
								Player owner = corpse.getOwner();

								if(owner.isOnline() && team.timerGoing())
								{
									MRPlayer mOwner = MRPlayer.getMRPlayer(owner);
									owner.teleport(corpse.getLocation());
									owner.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(2 * 10).setAmplifier(10));
									owner.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(Integer.MAX_VALUE).setAmplifier(1));
									owner.addEffect(Effect.getEffect(Effect.WEAKNESS).setDuration(Integer.MAX_VALUE).setAmplifier(1));
									owner.getInventory().clearAll();
									mOwner.removeAllItems();
									
									MRMain.getInstance().getMRGameConfig().giveItem(owner);
									addZombie(owner);
									
									team.removeCorpse(corpse);
									team.removeSpectator(owner);
									
									for(Player players : team.getPlayers())
									{
										String reviveMessage = message.replace("%s1", player.getName()).replace("%s2", owner.getName());
										players.sendMessage(reviveMessage);
										players.getLevel().addSound(players, Sound.MOB_WITHER_DEATH, 1F, 1F, players);
									}
									
									item.close();
									task.cancel();
									mPlayer.removeGameTask(task);
								}
								break;
							}
						}
					}
				});
				mPlayer.addGameTask(task);
			}
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
