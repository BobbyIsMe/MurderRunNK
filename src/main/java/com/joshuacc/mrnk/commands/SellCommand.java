package com.joshuacc.mrnk.commands;

import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.menus.FormMenu.GameMenus;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

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
					GameMenus.SELLITEMSMENU.getFormMenu().open(target);
				}
			}
			
			break;
		}
		return true;
	}
}
