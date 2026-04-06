package com.joshuacc.mrnk.perks;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerToggleSprintEvent;
import cn.nukkit.potion.Effect;

public class MysteriousHeadband extends PerkBase 
{
	@Override
	public String getName() 
	{
		return "MysteriousHeadband";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/mysterious_headband";
	}

	@Override
	public int getPrice() 
	{
		return 300;
	}

	@Override
	public String getPerkDesc() 
	{
		return "When sprinting, &bgain Speed III for 4 seconds&f to outrun the murderer!\n\n&7Cooldown: &a2 minutes";
	}

	@Override
	public String getPerkName() 
	{
		return "&l&fMysterious Headband";
	}

	@EventHandler
	public void onSprint(PlayerToggleSprintEvent event)
	{
		Player player = event.getPlayer();
		MRPlayer mPlayer  = MRPlayer.getMRPlayer(player);
		if(this.containsPlayer(player) && mPlayer != null && mPlayer.getMapTeam().timerGoing())
		{
			if(!PerkDelay.getInstance().hasDelay(this, player))
			{
				player.addEffect(Effect.getEffect(Effect.SPEED).setDuration(4 * 20).setAmplifier(2));
				PerkDelay.getInstance().putDelay(this, player, 120, () -> {
					player.sendMessage(MRMain.getInstance().getMRPerksConfig().getDelayIndicatorMessage(getPerkName()));
				});
			} else {
				player.sendMessage(MRMain.getInstance().getMRPerksConfig().getCooldownMessage(getPerkName(), PerkDelay.getInstance().secondsLeft(this, player)));
			}
		}
	}
}
