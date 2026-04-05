package com.joshuacc.mrnk.perks;

import com.joshuacc.mrnk.events.GameEndEvent;
import com.joshuacc.mrnk.events.GameStartEvent;
import com.joshuacc.mrnk.events.GameStartEvent.GameAttribute;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerToggleFlightEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;

public class DoubleJump extends PerkBase 
{

	@Override
	public String getName() 
	{
		return "DoubleJump";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/double_jump";
	}

	@Override
	public int getPrice() 
	{
		return 400;
	}

	@Override
	public String getPerkDesc() 
	{
		return "Press jump twice to &bdouble jump&f to catch your prey from above!";
	}

	@Override
	public String getPerkName() 
	{
		return "&lDouble Jump";
	}
	
	@EventHandler
	public void onGameStart(GameStartEvent event)
	{
		Player killer = event.getTeam().getKiller();
		if(killer != null && event.getGameAttribute() == GameAttribute.ROUND && this.containsPlayer(killer))
		{
			killer.getAdventureSettings().set(Type.ALLOW_FLIGHT, true);
			killer.getAdventureSettings().update();
		}
	}
	
	@EventHandler
	public void onGameEnd(GameEndEvent event)
	{
		Player killer = event.getTeam().getKiller();
		if (killer != null)
		{
			killer.getAdventureSettings().set(Type.ALLOW_FLIGHT, false);
			killer.getAdventureSettings().update();
		}
	}
	
	@EventHandler
	public void onFly(PlayerToggleFlightEvent event)
	{
		Player player = event.getPlayer();
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(this.containsPlayer(player) && mPlayer != null && player.getGamemode() == 0) 
		{
			player.getAdventureSettings().set(Type.ALLOW_FLIGHT, false);
			player.getAdventureSettings().set(Type.FLYING, false);
			player.getLevel().addSound(player, Sound.MOB_ZOMBIE_UNFECT, 1F, 2F);
			player.getAdventureSettings().update();
			event.setCancelled(true);
			Vector3 vec = player.getDirectionVector().setY(1).multiply(1);
			player.setMotion(vec);
			mPlayer.addDelayedTask(() -> {
				if(!mPlayer.getMapTeam().timerGoing())
					return;
				
				player.getLevel().addSound(player, Sound.MOB_WITHER_SHOOT, 1F, 2F);
				player.getAdventureSettings().set(Type.ALLOW_FLIGHT, true);
				player.getAdventureSettings().update();
			}, 10);
		}
	}
}
