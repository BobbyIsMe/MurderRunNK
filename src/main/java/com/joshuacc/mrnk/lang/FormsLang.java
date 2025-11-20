package com.joshuacc.mrnk.lang;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public enum FormsLang {

	//Placeholder
	//%l - Map Name
	//%n - Number

	//Map Selector
	SELMAPNAME("Map-Selector.Map Name", "&6» &2%l"),
	SELMAPNUM("Map-Selector.Available Maps", "&8Available Maps: %n"),

	//Config both maps and lobbies
	CONNOMAPS("Config-Texts.NoMaps", "&4No maps to configure."),
	CONRELOADMAPS("Config-Texts.ReloadMaps", "&aSuccessfully reloaded maps!"),
	CONTITLE("Config-Texts.Title", "Config Maps or Lobbies"),
	CONDESC("Config-Texts.Description", "Here you can select what to configure!"),
	CONTPLOBBY("Config-Texts.Tp-Lobby", "&6» &aTeleport to Lobby Location"),
	CONLOBBY("Config-Texts.Lobby", "&6» &5Set Lobby Location"),
	CONWLOBBY("Config-Texts.W-Lobby", "&6» &3Set Queue Lobby Location"),
	CONTYPE("Config-Texts.Type", "&6» &2Edit - &l%l"),

	//Editing Map Menu
	EDITTITLE("Edit-Texts.Title", "Editing Map - %l"),
	EDITDESC("Edit-Texts.Description", "Here you can see the menu on what you can edit for the map!"),
	EDITEXIT("Edit-Texts.Exit", "&6» &4Exit Config"),
	EDITTEL("Edit-Texts.Teleport Map", "&6» &aTeleport to Map"),
	EDITMAP("Edit-Texts.Config Map", "&6» &eConfig Map Settings"),
	EDITSLOC("Edit-Texts.S Location", "&6» &fSet Survivor Location"),
	EDITMLOC("Edit-Texts.M Location", "&6» &fSet Murderer Location"),
	EDITGLOC("Edit-Texts.G Location", "&6» &fSet Game End Location"),
	EDITVLOC("Edit-Texts.V Location", "&6» &fSet Vehicle Location"),
	EDITENABLE("Edit-Texts.Enable", "Enable"),

	//Settings Map Menu
	SNGTITLE("Settings-Texts.Title", "Settings For - %l"),
	SNGDESC("Settings-Texts.Description", "Here you can change settings for the arena it has to function properly!"),
	SNGPREP("Settings-Texts.Preparing Time", "&6» &a&lPreparing Time"),
	SNGTIME("Settings-Texts.Time Limit", "&6» &a&lTime Limit"),
	SNGPOINTS("Settings-Texts.Points Limit", "&6» &a&lPoints Limit"),
	SNGMINI("Settings-Texts.Minimum Players", "&6» &a&lMinimum Players"),
	SNGMAX("Settings-Texts.Maximum Players", "&6» &a&lMaximum Players"),
	
	//Survivor Items Category Menu
	SURVITEMSTITE("Survivor-Items.Title", "Survivor Items Category"),
	SURVITEMSDESC("Survivor-Items.Description", "Choose an item category"),
	SURVITEMSTRAPS("Survivor-Items.Traps", "&6» &eTraps"),
	SURVITEMSARMOR("Survivor-Items.Armor", "&6» &eArmor"),
	SURVITEMSPOTION("Survivor-Items.Potion", "&6» &ePotions");
	

	private String key;
	private String val;
	private static Config CONFIG;

	FormsLang(String key, String val)
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
