package com.joshuacc.mrnk.utils;

import java.util.ArrayList;
import java.util.Iterator;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public class ItemParticle {

	private final ArrayList<EntityItem> particles;
	
	private static final ItemParticle instance = new ItemParticle();
	
	private ItemParticle()
	{
		 particles = new ArrayList<>();
		 Server.getInstance().getScheduler().scheduleRepeatingTask(MRMain.getInstance(), () -> {

			    Iterator<EntityItem> iterator = particles.iterator();
			    
			    while(iterator.hasNext())
			    {
			    	EntityItem item = iterator.next();
			    	if(item.isClosed()) {
		                iterator.remove();
		                Player owner = Server.getInstance().getPlayer(item.getItem().getNamedTag().getString("Owner"));
						if(owner != null && owner.isOnline())
						{
							MRPlayer mPlayer = MRPlayer.getMRPlayer(owner);
							if(mPlayer != null)
							{
								mPlayer.removeDropItem(item);
							}
						}
		                continue;
		            }
		            
		            CompoundTag tag = item.getItem().getNamedTag();
		            int cooldown = tag.getInt("Cooldown");
		            
		            if(cooldown <= 0) {
		                tag.putInt("Cooldown", 2);
		                
		                final Vector3 pos = new Vector3(item.x, item.y, item.z);
		                final int particleId = tag.getInt("Particle");
		                final Level level = item.getLevel();
		                
		                Particle particle = new GenericParticle(pos, particleId);
		                for (int x = 0; x < 5; x++) {
		                    particle.setComponents(
		                        pos.x + randomWithRange(-0.3, 0.3),
		                        pos.y + randomWithRange(0.1, 0.2),
		                        pos.z + randomWithRange(-0.3, 0.3)
		                    );
		                    
		                    level.addParticle(particle);
		                }
		            } else {
		                tag.putInt("Cooldown", cooldown - 1);
		            }
			    }
		 }, 5);
	}
	
	public static ItemParticle getInstance() {
        return instance;
    }
	
	public void addParticle(EntityItem item)
	{
		particles.add(item);
	}
	
	public void addParticle(Location loc, int p)
	{
		addParticle(loc, p, 50);
	}

	public void addParticle(Location loc, int p, int size)
	{
		Particle particle = new GenericParticle(loc, p);
		for (int x = 0; x < size; x++) {
			particle.setComponents(
					loc.x + randomWithRange(-0.7,0.4),
					loc.y + randomWithRange(0.1,0.6),
					loc.z + randomWithRange(-0.5,0.6)
					);
			loc.getLevel().addParticle(particle);
		}
	}
	
	public void removeParticle(EntityItem item)
	{
		particles.remove(item);
	}
	
	public double randomWithRange(double min, double max)
	{
		double range = Math.abs(max - min);     
		return (Math.random() * range) + (min <= max ? min : max);
	}
}
