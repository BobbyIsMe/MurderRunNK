package com.joshuacc.mrnk.npc;

import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class NPCLobby extends NPCHuman {

	private final MRMain main;
	public NPCLobby(MRMain main, FullChunk chunk, CompoundTag nbt) 
	{
		super(chunk, nbt);
		this.main = main;
	}

	@Override
	public void onInteract(Player player) 
	{
		if(player.getInventory().getItemInHand().getId() == Item.GOLD_AXE)
		{
			main.getMRLobbyConfig().removePlayer(this);
			return;
		}

		String name = this.namedTag.getString("Command");
		name = name.replaceAll("%p", player.getName());
		Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), name);
	}

}
