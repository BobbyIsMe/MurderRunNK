package com.joshuacc.mrnk.traps;

import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.GameTask;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;

public class Pinpoint extends TrapClick {

	@Override
	public int getDelay() 
	{
		return 60;
	}

	@Override
	protected boolean performClickAbility(Player player) 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		MRTeam team = mPlayer.getMapTeam();
		if(team.getSurvivors().isEmpty())
			return false;
		
		GameTask task = new GameTask(15);
		task.addLoopTask(() -> {
			if(team.getSurvivors().isEmpty())
			{
				task.cancel();
				mPlayer.removeGameTask(task);
				return;
			}
			for(Player survivor : team.getSurvivors())
			{
				createBeam(player, survivor);
			}
		});
		mPlayer.addGameTask(task);
		return true;
	}

	@Override
	public String getName() 
	{
		return "Pinpoint";
	}

	@Override
	public String getIcon() 
	{
		return "textures/items/pinpoint";
	}

	@Override
	public int getPrice() 
	{
		return 150;
	}

	@Override
	public String getTrapDesc() 
	{
		return "Search for survivors by &bfollowing the path &fshown to you for &b15 seconds!\n\n&7Cooldown: &a1 minute";
	}

	@Override
	public int getItem() 
	{
		return Item.REDSTONE_LAMP;
	}

	@Override
	public String getTrapName() 
	{
		return "&2Pinpoint";
	}
	
	private void createBeam(Player loc, Player surv) {
		Vector3 a = loc.clone();
		Vector3 b = surv.clone();
	    Vector3 direction = b.subtract(a);
	    double length = direction.length();
	    direction = direction.normalize();

	    if(length > 35 || !loc.getLevel().equals(surv.getLevel()))
	    	return;
	    
	    for (double i = 0; i <= length; i += 1) {
	        Vector3 point = a.add(direction.multiply(i));
	        Particle particle = new GenericParticle(point, Particle.TYPE_REDSTONE);
			particle.setComponents(point.x, point.y, point.z);
			loc.getLevel().addParticle(particle);
	    }
	}

}
