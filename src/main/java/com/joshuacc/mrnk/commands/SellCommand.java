package com.joshuacc.mrnk.commands;

import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;

public class SellCommand extends Command {

	public SellCommand() {
		super("sell", "Sells current item held.");
		this.setPermission("mr.sell");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) 
	{
		if(sender instanceof Player)
			return false;
		switch(args.length)
		{
		case 0: 
		case 1: 
			Player target = Server.getInstance().getPlayer(args[0]);
			if(target == null)
				return false;
			
			MRPlayer mPlayer = MRPlayer.getMRPlayer(target);
			if(mPlayer != null)
			{
				MRTeam team = mPlayer.getMapTeam();
				if(team.onIntermission() && team.getKiller() != null) 
				{
					Item item = target.getInventory().getItemInHand();
					if(item == null || item.getNamedTag() == null)
						return false;
					
					int price = item.getNamedTag().getInt("Price");
					if (price != 0)
					{
						target.sendMessage(TextUtils.format(ConfigLang.SELLITEMSUCESS.toString().replace("%s", item.getName()).replace("%n", Integer.toString(price))));
						target.getInventory().decreaseCount(target.getInventory().getHeldItemIndex());
						mPlayer.addPoints(price);
					}
				}
			}
			
			break;
		}
		return true;
	}
}
