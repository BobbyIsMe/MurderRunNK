package com.joshuacc.mrnk.utils;

import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;

public class TextUtils {
	
	public String format(String line)
	{
		return TextFormat.colorize('&', MRMain.getPrefix()+" "+line);
	}
	
	public String formatNumber(String line, int number)
	{
		return TextFormat.colorize('&', line.replace("%num", Integer.toString(number)));
	}
	
	public String formatLevel(String line, String level)
	{
		return TextFormat.colorize('&', line.replace("%l", level));
	}
	
	public String formatPlayerMap(String line, Player player, String level)
	{
		return TextFormat.colorize('&', formatPlayer(line, player).replace("%l", level));
	}
	
	public String formatPlayer(String line, Player player)
	{
		return TextFormat.colorize('&', line.replace("%p", player.getName()));
	}
}
