package com.joshuacc.mrnk.lang;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public enum ConfigLang {

	//Placeholder
	//%l - Map Name
	//%p - Player Name
	//%n - Number

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
	NPCJOINPLAYERS("Npc-Join-Players Tag", "&a&l%n PLAYERS"),
	NPCNORMAL("Npc-Join-Normal", "&cNormal Mode"),
	NPCESCAPE("Npc-Join-Escape", "&cEscape Mode"),

	//Messages when you execute the command or config something in the map
	FAILCONFIG("Map-Config-Fail Message", "&cYou must set all locations for the map! (Vehicle is optional)"),
	NOAVAILABLEMAPS("No-Available-Maps Message", "&cNo available maps in the list!"),
	NOTAVAILABLEMAP("Not-Available-Map Message", "&cThis map is not joinable currently!"),
	NOTPLAYER("Not-Player Message", "&cYou must be a player to do this command!"),
	NOTNUMBER("Not-Number Message", "&cAt least one of the input isn't a number!"),
	SHORTARGUEMENTS("Not-Enough-Arguements Message", "&cNot enough arguements!"),
	NOLOBBYSPAWN("Lobby-Not-Found Message", "&cYou must set the lobby first to do this!"),
	NOLOBBYQUEUESPAWN("Queue-Not-Found Message", "&cYou must set the queue lobby first to do this!"),
	DISABLEMAP("Disable-Map Message", "&cYou must disable the map first to do this!"),
	SUCCESSJOIN("Successful-Join Message", "&aYou are now queued for %l!"),
	SUCCESSLOBBY("Successful-Made Lobby Message", "&aSuccessfully setup the lobby!"),
	SUCCESSWLOBBY("Successful-Made Wait-Lobby Message", "&aSuccessfully setup the queue world!"),
	CHANGESETTINGS("Settings-Config Message", "&aSuccessfully changed settings for %l!"),
	SLOCATIONMESSAGE("Setup S-Location Message", "&aSuccessfully set survivor location!"),
	MLOCATIONMESSAGE("Setup M-Location Message", "&aSuccessfully set murderer location!"),
	GLOCATIONMESSAGE("Setup G-Location Message", "&aSuccessfully set game-end location!"),
	VLOCATIONMESSAGE("Setup V-Location Message", "&aSuccessfully set vehicle location!"),

	//Messages shown while in a game
	PLAYERQUEUE("Player-Queue Message", "&eYou have been queued in %l!"),
	PLAYERLEAVE("Player-Leave Message", "&e%p has left the arena!"),
	QUEUEBAR("Queue-Bar Message", "&bYou are now being queued for &l%l"),
	MAPNOTIFYQUEUE("Map-Queue Message", "&a%p &ehas joined the queue."),
	MAPSTARTED("Map-Start Message", "&eThe game has officially started! From now on, you can't leave the game until the end!"),
	MAPTITLESTART("Start-Title", "&0&kIII&r &4&lMURDER RUN &r&0&kIII"),
	MAPSUBTSTART("Start-Subtitle","&eSurvive long while you can!"),
	MAPSELECT("Select-Killer Message", "&eSelecting random players in the arena to become the &4&lMURDERER!"),
	MAPRANDOM("Killer-Random", "&c&l%p"),
	MAPRANDFIN("Random-Finish", "&eis the murderer!"),
	KILLERLEAVE("Killer-Leave Message", "&eThe killer has left! Restarting round.."),
	INTERMISSION("Intermission Messsage", "&eYou have &a%n seconds &eto buy items from the Traders!"),
	INTERCOUNT("Inter-Count Message", "&eIntermission will end in &a%ns!"),
	MURDERANNOUNCE("Rel-Murderer Message", "&eYou have &a%n seconds &eto prepare until the &c&lMURDERER &r&ewill be released!"),
	MURDCOUNT("Murd-Count Message", "&eThe &c&lMURDERER &r&ewill be released in &a%ns!"),
	RELEASEMURD("Release-Mur Message", "&eThe &c&lMURDERER &r&ehas been &areleased.."),
	SURVIVORWIN("Survivor-Win", "&a&lSURVIVORS WIN!"),
	MURDERERWIN("Murderer-Win", "&c&lMURDERER WINS!"),
	REASON1("Reason-1", "&eThe murderer ran out of time!"),
	REASON2("Reason-2", "&eAll survivors have died!"),
	WIN1("Win-1 Message", "&eThe &cmurderer &eran out of time to kill everyone, the &asurvivors &ewin!");

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
