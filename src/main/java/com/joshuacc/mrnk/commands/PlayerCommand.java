package com.joshuacc.mrnk.commands;

import com.joshuacc.mrnk.files.MRLobbyConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.npc.NPCHuman;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;

public class PlayerCommand extends Command {

	private MRLobbyConfig lobby;

	public PlayerCommand(MRMain main) {
		super("npcadd", "Adds necessary NPCs for players to see the map selector!");
		this.lobby = main.getMRLobbyConfig();
		this.setPermission("mr.npcadd");
		this.commandParameters.clear();
		this.commandParameters.put("default", new CommandParameter[] {
				CommandParameter.newEnum("options", new String[] {
						"normal", "escape", "unqueue", "shop", "sell"	
				})
		});
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) 
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ConfigLang.NOTPLAYER.toString());
			return false;
		}

		Player player = (Player) sender;

		if(!player.hasPermission("mr.npcadd"))
			return false;

		if(args.length == 0)
			player.sendMessage(ConfigLang.SHORTARGUEMENTS.toString());
		else if(args.length == 1)
		{
			String type = args[0];
			switch(type)
			{
			case "normal":
				lobby.addJoinNPCDetails(MapModes.NORMAL, createEntityMode(player, "openlist %p normal"));
				break;
				
			case "escape":
				lobby.addJoinNPCDetails(MapModes.ESCAPE, createEntityMode(player, "openlist %p escape"));
				break;
			case "unqueue":
				lobby.addJoinNPCDetails("Unqueue", createEntityLobby(ConfigLang.NPCUNQUEUE.toString(), player, "unqueue %p"));
				break;
			case "shop":
				lobby.addJoinNPCDetails("Shop", createEntityLobby(ConfigLang.NPCSHOP.toString(), player, "openlist %p shop"));
				break;
			case "sell":
				lobby.addJoinNPCDetails("Sell", createEntityLobby(ConfigLang.NPCSELL.toString(), player, "sell %p"));
				break;
			}
		}
		return true;
	}

	private Entity createEntityMode(Player player, String command)
	{
		return NPCHuman.createEntity("NPCLobby", ConfigLang.NPCJOINLIST.toString(), player, command, 1.5F);
	}
	
	private Entity createEntityLobby(String name, Player player, String command)
	{
		return NPCHuman.createEntity("NPCLobby", name, player, command, 1.5F);
	}
}
