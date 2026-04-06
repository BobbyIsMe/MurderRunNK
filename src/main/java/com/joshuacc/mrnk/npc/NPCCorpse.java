package com.joshuacc.mrnk.npc;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class NPCCorpse extends NPCHuman {

	private Player player;
	public NPCCorpse(FullChunk chunk, CompoundTag nbt) {
		super(chunk, nbt);
	}

	@Override
	public void onInteract(Player player) 
	{
		
	}
	
	public Player getOwner()
	{
		return player;
	}

	public void sleep(Player player)
	{
		this.setSwimming(true);
        this.player = player;
	}
}
