package com.joshuacc.mrnk.traps;

import java.util.ArrayList;

import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.utils.ItemParticle;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;

public class FullCounter extends TrapClick {

	private ArrayList<String> add = new ArrayList<>();
	private MRTrapsConfig traps;
	private String message;
	
	public FullCounter()
	{
		traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message", "&e&lFULL COUNTER!");
		message = traps.getString(getName(), "Message");
	}
	
	@Override
	public int getDelay() 
	{
		return 180;
	}

	@Override
	protected boolean performClickAbility(Player player) 
	{
		add.add(player.getName());
		Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {

			@Override
			public void onRun(int arg0) 
			{
				add.remove(player.getName());
			}

		}, 20 * 10);
		return true;
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event)
	{
		if(add.contains(event.getEntity().getName()))
		{
			Player player = (Player) event.getDamager();
			if(player.getName().equals(MRPlayer.getMRPlayer(player).getMapTeam().getKiller().getName()))
			{
				player.setMotion(player.getDirectionVector().multiply(-4));
				ItemParticle.getInstance().addParticle(player, Particle.TYPE_FLAME);
				player.getLevel().addSound(player, Sound.MOB_GHAST_FIREBALL);
				player.sendTitle(TextFormat.colorize(message));
			}
			event.setCancelled(true);
		}
	}

	@Override
	public String getName() 
	{
		return "FullCounter";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/full_counter";
	}

	@Override
	public int getPrice() 
	{
		return 300;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Use the ability to &blaunch the murderer away &ffrom you when they try to attack!";
	}

	@Override
	public Item getItem() 
	{
		return new Item(Item.GLOWSTONE_BLOCK);
	}

	@Override
	public String getTrapName() 
	{
		return "&eFull Counter";
	}

}
