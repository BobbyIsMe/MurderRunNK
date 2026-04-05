package com.joshuacc.mrnk.npc;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.entity.data.IntPositionEntityData;

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
		Vector3 pos = player;
		this.teleport(new Location(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, this.yaw, this.pitch, this.level), null);

        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, (int) pos.x, (int) pos.y, (int) pos.z));
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, true);
        this.player = player;
	}
}
