package com.joshuacc.mrnk.traps;

import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTraps;
import com.joshuacc.mrnk.utils.GameTask;
import com.joshuacc.mrnk.utils.TaskAct;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;

public class Portal extends TrapDrop {
	
	@Override
	public String getName() 
	{
		return "Portal";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/portal";
	}

	@Override
	public int getPrice() 
	{
		return 25;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Support yourself or others by &bactivating placed traps&f by &bdropping&f this item nearby other traps!";
	}

	@Override
	public int getItem() 
	{
		return Item.WOOL;
	}

	@Override
	public int getMeta()
	{
		return 10;
	}
	
	@Override
	public String getTrapName() 
	{
		return "&dPortal";
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
		return Particle.TYPE_FLAME;
	}
	
	@EventHandler
	public void onSpawnItem(ItemSpawnEvent event)
	{
		EntityItem item = event.getEntity();
		if(item.getItem().getName().equals(getTrapItemName()))
		{
			Player owner = Server.getInstance().getPlayer(item.getItem().getNamedTag().getString("Owner"));
			MRPlayer mPlayer = MRPlayer.getMRPlayer(owner);
			if(owner != null && mPlayer != null) {
				GameTask task = new GameTask(-1);
				task.addLoopTask(new TaskAct() 
				{

					@Override
					public void doTask() 
					{
						for(Entity ent : item.getLevel().getNearbyEntities(item.getBoundingBox().grow(0.5, 0.5, 0.5))) 
						{
							if(ent instanceof EntityItem) 
							{
								EntityItem eitem = (EntityItem) ent;
								Item e = eitem.getItem();
								if(e.getNamedTag() != null) 
								{
									MRTraps trap = MRTraps.getTrap(e.getNamedTag().getString("Trap"));
									if(trap instanceof TrapDrop && !trap.getName().equals(getName()))
									{
										((TrapDrop) trap).doAbility(owner, eitem);
										item.close();
										eitem.close();
										task.cancel();
										mPlayer.removeGameTask(task);
										break;
									}
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
