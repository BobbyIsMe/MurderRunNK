package com.joshuacc.mrnk.commands;

import com.joshuacc.mrnk.lang.ConfigLang;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

public class UpCommand extends Command {

	public UpCommand() {
		super("up", "Adds a block under the player!");
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
		
		player.getLevel().setBlock(player.getLocation().add(0, -1, 0), Block.get(Block.GLASS));
		return true;
	}
}
