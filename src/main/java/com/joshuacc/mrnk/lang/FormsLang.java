package com.joshuacc.mrnk.lang;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public enum FormsLang {

	//Placeholder
	//%l - Map Name
	//%n - Number
	//%s - String

	//Button essentials
	BACKBUTTON("Buttons.Back", "&6» &4Back"),
	FILTERBUTTON("Buttons.Filter", "&6» &aFilter"),
	PREVIOUSBUTTON("Buttons.Previous", "&6<< &4Previous"),
	NEXTBUTTON("Buttons.Next", "&4Next &6>>"),
	
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
	
	//General Filters
	FILTERTITLE("Filters.Title", "Filter Items"),
	FILTERDESC("Filters.Description", "Filter through relevant items"),
	SEARCHKEYWORD("Filters.Search-Keyword", "Search Keyword"),
	
	//Dropdown Filters
	SORTPRICE("Filters.Sort Price", "Sort By Price"),
	LOWTOHIGH("Filters.Low-To-High Prices", "Low To High Prices"),
	HIGHTOLOW("Filters.High-To-Low Prices", "High To Low Prices"),
	SORTCATEGORY("Filters.Sort Type", "Sort By Type"),
	ALLCATEGORY("Filters.All Category", "All"),
	TRAPCLICK("Filters.Trap Click", "Trap Click"),
	TRAPDROP("Filters.Trap Drop", "Trap Drop"),
	
	//Item Builder
	ITEMNAME("Item.Name", "&l%s1\n&r%s2"),
	ITEMPRICE("Item.Price", "&8Price: &2%n pts"),
	POINTSLEFT("Item.Points Left", "&8Points Left: &2%n pts"),
	CURRENTPAGE("Item.Current Page", "Page %n of %m"),
	
	//Survivor Items Category Menu
	SURVITEMSTITLE("Survivor-Items.Title", "Survivor Items Category"),
	SURVITEMSDESC("Survivor-Items.Description", "Choose an item category"),
	SURVITEMSTRAPS("Survivor-Items.Traps", "&6» &bTraps"),
	SURVITEMSARMOR("Survivor-Items.Armor", "&6» &bArmor"),
	SURVITEMSUTIL("Survivor-Items.Utilities", "&6» &bUtilities"),
	
	//Armor Shop Menu
	ARMORTITLE("Armor.Title", "Armor Shop"),
	ARMORDESC("Armor.Description", "Buy different armor pieces to protect yourself"),
	
	//Utilities Menu
	UTILTITLE("Utilities.Title", "Utility Shop"),
	UTILDESC("Utilities.Description", "Buy utilities to stall the murderer"),
	
	//Traps Menu
	TRAPSTITLE("Traps.Title", "Trap Shop"),
	TRAPSDESC("Traps.Desc", "Buy traps to help %s"),
	SURVTRAPSDESC("Traps.Surv Desc", "stall the murderer!"),
	MURDTRAPSDESC("Traps.Killer Desc", "find and kill everyone!"),
	TRAPNAME("Traps.Trap Name", "%s1 - %s2"),
	
	//Select Item Menu
	SELITEMNAME("Select-Item.Title", "Select Item"),
	SELITEMPURCHASE("Select-Trap.Purchase", "&6» &aPurchase"),
	
	//Murderer Items Category Menu
	MURDITEMSTITLE("Killer-Items.Title", "Murderer Items Category"),
	MURDITEMSDESC("Killer-Items.Description", "Choose an item category"),
	MURDITEMSTRAPS("Killer-Items.Traps", "&6» &bTraps"),
	;
	

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
