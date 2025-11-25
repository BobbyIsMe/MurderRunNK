package com.joshuacc.mrnk.commands;

import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.utils.FormUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class OpenListCommand extends Command {

	private FormUtils forms;

	public OpenListCommand(MRMain main) {
		super("openlist");
		this.setPermission("mr.openlist");
		this.forms = main.getFormUtil();
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
				forms.addMapsSelector(target, MapModes.NORMAL);
				break;
			case "escape":
				forms.addMapsSelector(target, MapModes.ESCAPE);
				break;
			}
			break;
		}
		return true;
	}

}
