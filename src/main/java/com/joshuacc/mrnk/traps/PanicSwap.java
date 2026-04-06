package com.joshuacc.mrnk.traps;

import java.util.Random;

import com.joshuacc.mrnk.files.MRTrapsConfig;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.ItemParticle;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.utils.TextFormat;

public class PanicSwap extends TrapClick 
{

	private String message;
	
	public PanicSwap()
	{
		MRTrapsConfig traps = MRMain.getInstance().getMRTrapsConfig();
		traps.putDefault(getName(), "Message", "&bYou have swapped positions with &a%s!");
		message = TextFormat.colorize(traps.getString(getName(), "Message"));
	}
	
	@Override
	public int getDelay() 
	{
		return 0;
	}

	@Override
	protected boolean performClickAbility(Player player) 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		MRTeam team = mPlayer.getMapTeam();
		Player target = team.getSurvivors().get(new Random().nextInt(team.getSurvivors().size()));
		Player killer = team.getKiller();
		if(team.getSurvivors().size() == 0 || killer == null || !killer.isOnline())
			return false;
		
		Location loc = killer.getLocation().clone();
		ItemParticle.getInstance().addParticle(target, Particle.TYPE_END_ROD, 10);
		ItemParticle.getInstance().addParticle(killer, Particle.TYPE_END_ROD, 10);
		killer.teleport(target);
		target.teleport(loc);
		killer.sendMessage(message.replace("%s", target.getName()));
		target.sendMessage(message.replace("%s", killer.getName()));
		killer.getLevel().addSound(killer, Sound.MOB_ENDERMEN_PORTAL, 1F, 1F);
		target.getLevel().addSound(target, Sound.MOB_ENDERMEN_PORTAL, 1F, 1F);
		
		return true;
	}

	@Override
	public String getName() 
	{
		return "PanicSwap";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/panic_swap";
	}

	@Override
	public int getPrice() 
	{
		return 500;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Confuse the murderer by &bswapping them with a random survivor!\n\n&7Cooldown: &aOne Time Use";
	}

	@Override
	public int getItem() 
	{
		return Item.AMETHYST_SHARD;
	}

	@Override
	public String getTrapName() 
	{
		return "&dPanic Swap";
	}

}
