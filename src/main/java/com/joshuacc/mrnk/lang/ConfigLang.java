package com.joshuacc.mrnk.lang;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public enum ConfigLang {
	
	//Placeholder
	//%l - Map Name
	//%p - Player Name
	//%num - Number

	//Prefix for some messages in-game to be shown
	PREFIXMESSAGE("Prefix Message", "&4&lMurder Run &6»&r"),
	
	//Nametags for the player to be changed
	LOBBYTAG("Player-Lobby-Tag", "&e%p"),
	QUEUETAG("Player-Queue-Tag", "&b[%l] %p"),
	SURVIVORTAG("Player-Survivor-Tag", "&a[SURVIVOR] %p"),
	KILLERTAG("Player-Killer-Tag", "&c[MURDERER] %p"),
	SPECTATORTAG("Player-Spectator-Tag", "&f[SPECTATOR] %p"),
	
	//NPC names
	NPCJOINLIST("NPC-Join-List Name", "&eClick to Join a &6&lMap!"),
	NPCJOINPLAYERS("Npc-Join-Players Tag", "&a&l%num PLAYERS"),
	NPCNORMAL("Npc-Join-Normal", "&cNormal Mode"),
	NPCESCAPE("Npc-Join-Escape", "&cEscape Mode"),
	
	//Messages when you execute the command or config something in the map
	NOAVAILABLEMAPS("No-Available-Maps Message", "&cNo available maps in the list!"),
	NOTAVAILABLEMAP("Not-Available-Map Message", "&cThis map is not joinable currently!"),
	NOTPLAYER("Not-Player Message", "&cYou must be a player to do this command!"),
	NOTNUMBER("Not-Number Message", "&cAt least one of the input isn't a number!"),
	SHORTARGUEMENTS("Not-Enough-Arguements Message", "&cNot enough arguements!"),
	DISABLEMAP("Disable-Map Message", "&cYou must disable the map first to do this!"),
	SUCCESSJOIN("Successful-Join Message", "&aYou are now queued for %l!"),
	SUCCESSLOBBY("Successful-Made Lobby Message", "&aSuccessfully setup the lobby!"),
	SUCCESSWLOBBY("Successful-Made Wait-Lobby Message", "&aSuccessfully setup the queue world!"),
	CHANGESETTINGS("Settings-Config Message", "&aSuccessfully changed settings for %l!"),
	SLOCATIONMESSAGE("Setup S-Location Message", "&aSuccessfully set survivor location!"),
	MLOCATIONMESSAGE("Setup M-Location Message", "&aSuccessfully set murderer location!"),
	GLOCATIONMESSAGE("Setup G-Location Message", "&aSuccessfully set game-end location!"),
	VLOCATIONMESSAGE("Setup V-Location Message", "&aSuccessfully set vehicle location!");
	
	private String key;
	private String val;
	private static Config CONFIG;
	
	ConfigLang(String key, String val)
	{
		this.key = key;
		this.val = val;
	}
	
	public static void setLines(Config config)
	{
		CONFIG = config;
	}
	
	@Override
	public String toString()
	{
		return TextFormat.colorize('&', CONFIG.getString(this.key, this.val));
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public String getValue()
	{
		return this.val;
	}
}
