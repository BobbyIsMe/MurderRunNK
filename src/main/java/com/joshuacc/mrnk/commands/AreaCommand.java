package com.joshuacc.mrnk.commands;

import com.joshuacc.mrnk.files.MRAreasConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

public class AreaCommand extends Command {
	
	private MRAreasConfig areas;

	public AreaCommand(MRMain main) {
		super("area", "Creates a protective area.");
		this.areas = main.getMRAreasConfig();
		this.setPermission("mr.area");
		this.commandParameters.clear();
		this.commandParameters.put("default", new CommandParameter[] {
				CommandParameter.newEnum("options", new String[] {
						"add", "remove", "wand"
				})
		});
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) 
	{
		if(!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		String option = args[0];
		switch(args.length)
		{
		case 0:
			ConfigLang.SHORTARGUEMENTS.toString();
			break;
		case 1: 
		{
			if(option.equals("wand"))
			{
				areas.giveAreaItem(player);
			}
			else if(option.equals("remove"))
			{
				areas.removeArea(player);
			} else
			{
				ConfigLang.SHORTARGUEMENTS.toString();
			}
		}
			break;
		case 2:
		case 3:
			ConfigLang.SHORTARGUEMENTS.toString();
			break;
		case 4:
			if(option.equals("add"))
			{
				areas.addArea(player, args[1], args[2], args[3]);
			}
			break;
		}
		return false;
	}
}
