package com.joshuacc.mrnk.commands;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.menus.FormMenu.GameMenus;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class OpenListCommand extends Command {

	public OpenListCommand(MRMain main) {
		super("openlist");
		this.setPermission("mr.openlist");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) 
	{
		if(sender instanceof Player)
			return false;

		switch(args.length)
		{
		case 0: 
		case 1: break;
		case 2:

			Player target = Server.getInstance().getPlayer(args[0]);
			if(target == null)
				return false;

			switch(args[1])
			{
			case "normal":
				GameMenus.SELECTMAPNORMALMENU.getFormMenu().open(target);
				break;
			case "escape":
				GameMenus.SELECTMAPESCAPEMENU.getFormMenu().open(target);
				break;
			case "shop":
			{
				MRPlayer mPlayer = MRPlayer.getMRPlayer(target);
				if(mPlayer != null)
				{
					MRTeam team = mPlayer.getMapTeam();
					Player killer = team.getKiller();
					if(team.onIntermission() && killer != null)
						if (target.getName().equals(killer.getName())) 
						{
//							GameMenus.SURVITEMSMENU.getFormMenu().open(target);
							GameMenus.MURDITEMSMENU.getFormMenu().open(target);
						} else {
							GameMenus.SURVITEMSMENU.getFormMenu().open(target);
						}
				}
			}
				break;
			}
			break;
		}
		return true;
	}

}
