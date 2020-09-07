package com.joshuacc.mrnk.events;

import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class GameEndEvent extends Event {

	private static final HandlerList list = new HandlerList();
	private MRTeam team;
	private WinType type;

	public enum WinType
	{
		OUT_OF_TIME("Reason-1", "Win-1 Message"),
		KILL_ALL("Reason-2", "Win-2 Message"),
		SURVIVORS_LEAVE("Reason-3", "Win-3 Message"),
		KILLER_LEAVE("Reason-4", "Win-4 Message"),
		VEHICLE_SUCCESS("", "");

		private String subtitle;
		private String message;
		private static Config LANG;

		WinType(String subtitle, String message)
		{
			this.subtitle = subtitle;
			this.message = message;
		}

		public static void setLines(Config lang)
		{
			LANG = lang;
		}

		public String getSubtitle()
		{
			return TextFormat.colorize(LANG.getString(subtitle));
		}

		public String getMessage()
		{
			return TextUtils.format(LANG.getString(message));
		}

		public String getMessage(Player player)
		{
			return TextUtils.formatPlayer(getMessage(), player);
		}
	}

	public GameEndEvent(MRTeam team, WinType type)
	{
		this.team = team;
		this.type = type;
	}

	public MRTeam getTeam()
	{
		return team;
	}

	public WinType getWinType()
	{
		return type;
	}

	public static HandlerList getHandlers()
	{
		return list;
	}

	public static HandlerList getHandlerList()
	{
		return list;
	}
}
