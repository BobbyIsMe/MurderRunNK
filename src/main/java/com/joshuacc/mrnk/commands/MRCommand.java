package com.joshuacc.mrnk.commands;

import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.utils.FormUtils;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

public class MRCommand extends Command {

	private FormUtils formUtil;

	public MRCommand(MRMain main) {
		super("mr", "Sets up the entire minigame!");
		this.setPermission("mr.config");
		this.commandParameters.clear();
		this.commandParameters.put("default", new CommandParameter[] {
				new CommandParameter("options", new String[] {
						"config", "traps"
				})
		});
		
		this.formUtil = main.getFormUtil();
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) 
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(TextUtils.format(ConfigLang.NOTPLAYER.toString()));
			return false;
		}

		Player player = (Player) sender;
		
		if(!player.hasPermission("mr.config"))
			return false;

		switch(args.length)
		{
		case 0: player.sendMessage(TextUtils.format(ConfigLang.SHORTARGUEMENTS.toString())); break;
		case 1: 
			String type = args[0];
			switch(type)
			{
			case "config":
				formUtil.addConfigMenu(player);
				break;
			case "traps":
				
				break;
			case "reload":
				formUtil.initializeMaps();
				player.sendMessage(FormsLang.CONRELOADMAPS.toString());
				break;
			}
			break;
		}
		return true;
	}
}
