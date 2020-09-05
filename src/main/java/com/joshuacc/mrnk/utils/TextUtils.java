package com.joshuacc.mrnk.utils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.joshuacc.mrnk.main.MRMain;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;

public class TextUtils {

	private static SimpleDateFormat format = new SimpleDateFormat("m:ss");
	static {
		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
	}

	public static String format(String line)
	{
		return TextFormat.colorize('&', MRMain.getPrefix()+" "+line);
	}

	public static String formatNumber(String line, int number)
	{
		return TextFormat.colorize('&', line.replace("%n", Integer.toString(number)));
	}

	public static String formatLevel(String line, String level)
	{
		return TextFormat.colorize('&', line.replace("%l", level));
	}

	public static String formatPlayerMap(String line, Player player, String level)
	{
		return TextFormat.colorize('&', formatPlayer(line, player).replace("%l", level));
	}

	public static String formatPlayer(String line, Player player)
	{
		return TextFormat.colorize('&', line.replace("%p", player.getName()));
	}

	public static String getTimeFormat(int time)
	{
		return format.format(time * 1000);
	}
}
