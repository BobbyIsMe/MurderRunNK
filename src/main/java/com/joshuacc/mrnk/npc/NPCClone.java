package com.joshuacc.mrnk.npc;

import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

public class NPCClone extends NPCHuman {

	public NPCClone(FullChunk chunk, CompoundTag nbt) {
		super(chunk, nbt);
	}

	@Override
	public void onInteract(Player player) 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(mPlayer != null && mPlayer.getMapTeam().getKiller() == player)
		{
			player.addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(3 * 20).setAmplifier(10));
			this.close();
		}
	}

}
