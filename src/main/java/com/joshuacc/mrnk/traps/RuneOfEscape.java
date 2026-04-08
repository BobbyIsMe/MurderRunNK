package com.joshuacc.mrnk.traps;

import java.util.HashMap;

import com.joshuacc.mrnk.events.TrapTriggeredEvent;
import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.GameTask;
import com.joshuacc.mrnk.utils.GlowEffect;
import com.joshuacc.mrnk.utils.ItemDelay;
import com.joshuacc.mrnk.utils.ItemParticle;
import com.joshuacc.mrnk.utils.TaskAct;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.utils.TextFormat;

public class RuneOfEscape extends TrapClick 
{
	private HashMap<MRTeam, Location> location;
	private String message;
	
	public RuneOfEscape()
	{
		location = new HashMap<>();
		MRTrapsConfig traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message", "&aYou have placed your rune.");
		message = TextFormat.colorize(traps.getString(getName(), "Message"));
	}
	
	@Override
	public int getDelay() 
	{
		return -1;
	}

	@Override
	protected boolean performClickAbility(Player player) 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		location.put(mPlayer.getMapTeam(), player);
		GameTask task = new GameTask(0);
		task.addLoopTask(new TaskAct() {

			@Override
			public void doTask() 
			{
				MRTeam team = mPlayer.getMapTeam();
				if(!location.containsKey(team))
				{
					task.cancel();
					return;
				}
				
				ItemParticle.getInstance().addParticle(location.get(team), Particle.TYPE_MOB_SPELL, 10);
			}
			
		});
		mPlayer.addGameTask(task);
		return false;
	}

	@Override
	public String getName() 
	{
		return "RuneOfEscape";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/rune_of_escape";
	}

	@Override
	public int getPrice() 
	{
		return 300;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Place a rune that you can &bteleport to&f that also &bremoves debuffs&f when you want to save time running around the map!\n\n&7Cooldown: &a2 minutes.";
	}

	@Override
	public int getItem() 
	{
		return Item.EMERALD;
	}

	@Override
	public String getTrapName() 
	{
		return "&eRune of Escape";
	}
	
	@EventHandler
	public void onClick(final PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		Item item = event.getItem();
		if(item == null)
			return;
		if (mPlayer != null && item.getCustomName().equals(getTrapItemName())) 
		{
			MRTeam team = MRPlayer.getMRPlayer(player).getMapTeam();
			Player killer = team.getKiller();
			if(killer != null && killer.getLevel().equals(team.getMapLevel())) 
			{
				TrapTriggeredEvent trapEvent = new TrapTriggeredEvent(player, this, 120);
				Server.getInstance().getPluginManager().callEvent(trapEvent);
				if(!trapEvent.isCancelled())
				{
					if (!ItemDelay.getInstance().onCooldown(player, this) && location.get(team) != null) 
					{
						player.setMovementSpeed(0.1F);
						player.removeAllEffects();
						player.sendMessage(message);
						player.teleport(location.get(team));
						location.remove(team);
						GlowEffect.getInstance().addGlowingEffect(player, 5, 3);
						ItemDelay.getInstance().addCooldown(player, this, trapEvent.getDelay());
					}
				}
			} else
				player.sendMessage(TextUtils.format(ConfigLang.MURDERERNOTREL.toString()));
			event.setCancelled(true);
		}
	}
}
