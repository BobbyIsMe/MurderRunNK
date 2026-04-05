package com.joshuacc.mrnk.traps;

import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.npc.NPCCorpse;
import com.joshuacc.mrnk.utils.GameTask;
import com.joshuacc.mrnk.utils.TaskAct;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;

public class Revive extends TrapDrop {

	private String message;
	
	public Revive()
	{
		MRTrapsConfig traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message", "&a%s1 &bhas revived &a%s2!");
		message = TextFormat.colorize(traps.getString(getName(), "Message"));
	}
	
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
		return Particle.TYPE_DUST;
	}

	@Override
	public String getName() 
	{
		return "Revive";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/revive";
	}

	@Override
	public int getPrice() 
	{
		return 700;
	}

	@Override
	public String getTrapDesc() 
	{
		return "&bRevive other players&f by &bdropping&f this item nearby other corpses!";
	}

	@Override
	public int getItem() 
	{
		return Item.NETHER_STAR;
	}

	@Override
	public String getTrapName() 
	{
		return "&l&fRevive";
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

								if(owner.isOnline())
								{
									MRPlayer mOwner = MRPlayer.getMRPlayer(owner);
									owner.teleport(corpse.getLocation());
									owner.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(2 * 10).setAmplifier(10));
									owner.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(2 * 10).setAmplifier(10));
									owner.setGamemode(0);
									owner.getInventory().clearAll();
									
									for(Item item : mOwner.getInventoryItems())
									{
										owner.getInventory().addItem(item);
									}
									
									mOwner.removeAllItems();
									
									team.removeCorpse(corpse);
									team.removeSpectator(owner);
									team.addSurvivor(owner);
									team.updateEntry(team.getPlayBoard().getInt("Survivors Left"),
											TextUtils.formatLine(team.getPlayBoard().getString("Survivors Left-Line"),
													Integer.toString(team.getSurvivors().size())));
									team.sendScoreboardTip();
									
									for(Player players : team.getPlayers())
									{
										String reviveMessage = message.replace("%s1", owner.getName()).replace("%s2", owner.getName());
										players.sendMessage(reviveMessage);
										players.getLevel().addSound(players, Sound.MOB_ZOMBIE_UNFECT, 1F, 1F, players);
									}
									
									item.close();
									task.cancel();
									mPlayer.removeGameTask(task);
									break;
								}
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
