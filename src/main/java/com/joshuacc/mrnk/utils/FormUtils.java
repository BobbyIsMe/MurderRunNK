package com.joshuacc.mrnk.utils;

import java.util.HashMap;
import java.util.UUID;

import com.joshuacc.mrnk.events.PlayerJoinGameEvent;
import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.files.MRLobbyConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRPlayer;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.main.MRTeam.MapModes;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;

public class FormUtils {

	private TextUtils util;
	private MRMain main;
	private HashMap<UUID,String> editLevel = new HashMap<>();
	private HashMap<Integer,String> maps = new HashMap<>();
	private HashMap<Player, HashMap<Integer,String>> idMap = new HashMap<>();

	public FormUtils(MRMain main)
	{
		this.util = main.getTextUtil();
		this.main = main;

		if(main.getMaps() != null)
			for(int i = 0; i < main.getMaps().length; i++)
				maps.put(i+2, main.getMaps()[i]);
		else
			maps = null;
	}

	public void addConfigMenu(Player player)
	{
		FormWindowSimple configMenu = new FormWindowSimple(FormsLang.CONTITLE.toString(), FormsLang.CONDESC.toString());
		configMenu.addButton(new ElementButton(FormsLang.CONLOBBY.toString()));
		configMenu.addButton(new ElementButton(FormsLang.CONWLOBBY.toString()));
		for(String maps : maps.values())
			configMenu.addButton(new ElementButton(util.formatLevel(FormsLang.CONTYPE.toString(), maps)));
		player.showFormWindow(configMenu, 100);
	}

	public void addConfigMapForm(Player player, String level)
	{
		FormWindowSimple configForm = new FormWindowSimple(util.formatLevel(FormsLang.EDITTITLE.toString(), level), FormsLang.EDITDESC.toString());
		configForm.addButton(new ElementButton(FormsLang.EDITTEL.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITMAP.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITSLOC.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITMLOC.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITGLOC.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITVLOC.toString()));

		if(main.getMapConfigs().get(level).isMapEnabled())
			configForm.addButton(new ElementButton("�4�l"+FormsLang.EDITENABLE.toString(), new ElementButtonImageData("url", "https://icons.iconarchive.com/icons/paomedia/small-n-flat/128/sign-error-icon.png")));
		else
			configForm.addButton(new ElementButton("�2�l"+FormsLang.EDITENABLE.toString(), new ElementButtonImageData("url", "https://icons.iconarchive.com/icons/paomedia/small-n-flat/128/sign-check-icon.png")));
		player.showFormWindow(configForm);
	}

	public void addSettingsMapForm(Player player, String level)
	{
		MRArenasConfig config = main.getMapConfigs().get(editLevel.get(player.getUniqueId()));
		FormWindowCustom editForm = new FormWindowCustom(util.formatLevel(FormsLang.SNGTITLE.toString(), level));
		editForm.addElement(new ElementLabel(FormsLang.SNGDESC.toString()));
		editForm.addElement(new ElementInput(FormsLang.SNGNORM.toString(), "normal", config.getInt("Normal Multiples")));
		editForm.addElement(new ElementInput(FormsLang.SNGESC.toString(), "escape", config.getInt("Escape Multiples")));
		editForm.addElement(new ElementInput(FormsLang.SNGPREP.toString(), "prepare", config.getInt("Preparing Time")));
		editForm.addElement(new ElementInput(FormsLang.SNGTIME.toString(), "time", config.getInt("Time Limit")));
		editForm.addElement(new ElementInput(FormsLang.SNGPOINTS.toString(), "points", config.getInt("Points Limit")));
		editForm.addElement(new ElementInput(FormsLang.SNGMINI.toString(), "mini", config.getInt("Minimum Players")));
		editForm.addElement(new ElementInput(FormsLang.SNGMAX.toString(), "max", config.getInt("Maximum Players")));
		player.showFormWindow(editForm, 102);
	}

	public void addMapsSelector(Player player, MapModes type)
	{
		if(checkLobbySpawns(player))
			return;

		if(maps != null)
		{
			int formId = type.getID();
			int id = 0;
			FormWindowSimple mapSelector = new FormWindowSimple(type.getTitle(), type.getDesc());

			idMap.put(player, new HashMap<>());
			for(String maps : main.getMaps())
			{
				MRArenasConfig config = main.getMapConfigs().get(maps);

				if(config.isMapEnabled())
				{
					int normal = config.getMultiples(type);

					if(normal != 0)
					{
						int all = 0;

						for(int i = 1; i <= normal; i++)
						{
							MapState mode = MRTeam.getMapTeamByID(maps, i, type).getState();
							if(mode == MapState.READY || mode == MapState.STARTING)
								all++;
						}

						idMap.get(player).put(id, maps);
						mapSelector.addButton(new ElementButton(util.formatLevel(FormsLang.SELMAPNAME.toString(), maps)+"\n"+util.formatNumber(FormsLang.SELMAPNUM.toString(), all)));
						id++;
					}
				}
			}

			player.showFormWindow(mapSelector, formId);
		} else
			player.sendMessage(ConfigLang.NOAVAILABLEMAPS.toString());
	}

	public void handleAllResponse(Player player, int id, FormWindow window, FormResponse response)
	{
		if(window.wasClosed())
		{
			editLevel.remove(player.getUniqueId());
			idMap.remove(player);
			return;
		}

		switch(id) {
		case 100:
			handleConfigMenuForm(player, (FormResponseSimple) response);
			return;
		case 102:
			handleSettingsMapForm(player, (FormResponseCustom) response);
			return;
		case 103:
			handleMapsSelector(player, (FormResponseSimple) response, MapModes.NORMAL);
			return;
		case 104:
			handleMapsSelector(player, (FormResponseSimple) response, MapModes.ESCAPE);
			return;
		}

		if(editLevel.get(player.getUniqueId()) == null)
			return;

		handleConfigMapForm(player, (FormResponseSimple) response);
	}

	private void handleConfigMenuForm(Player player, FormResponseSimple response)
	{
		int id = response.getClickedButtonId();
		MRLobbyConfig lobby = main.getMRLobbyConfig();
		if(id == 0)
		{
			lobby.setupLobbyLocation(player, true);
			player.sendMessage(ConfigLang.SUCCESSLOBBY.toString());
		} else if(id == 1) {
			lobby.setupLobbyLocation(player, false);
			player.sendMessage(ConfigLang.SUCCESSWLOBBY.toString());
		} else if(id >= 2) {

			if(checkLobbySpawns(player))
				return;

			String level = maps.get(id);
			editLevel.put(player.getUniqueId(), level);
			addConfigMapForm(player, level);
		}
	}

	public boolean checkLobbySpawns(Player player)
	{
		MRLobbyConfig lobby = main.getMRLobbyConfig();
		if(lobby.getMainLobbyLocation().getLevel() == null)
		{
			player.sendMessage(ConfigLang.NOLOBBYSPAWN.toString());
			return true;
		}

		if(lobby.getQueueLobbyLocation().getLevel() == null)
		{
			player.sendMessage(ConfigLang.NOLOBBYQUEUESPAWN.toString());
			return true;
		}

		return false;
	}

	private void handleSettingsMapForm(Player player, FormResponseCustom response)
	{
		String map = editLevel.get(player.getUniqueId());
		MRArenasConfig config = main.getMapConfigs().get(map);
		try {
			config.setValue("Normal Multiples", response.getInputResponse(1));
			config.setValue("Escape Multiples", response.getInputResponse(2));
			config.setValue("Preparing Time", response.getInputResponse(3));
			config.setValue("Time Limit", response.getInputResponse(4));
			config.setValue("Points Limit", response.getInputResponse(5));
			config.setValue("Minimum Players", response.getInputResponse(6));
			config.setValue("Maximum Players", response.getInputResponse(7));
			config.getConfig().save();
			player.sendMessage(util.formatLevel(ConfigLang.CHANGESETTINGS.toString(), map));
		} catch(Exception e) {
			player.sendMessage(ConfigLang.NOTNUMBER.toString());
		}

		addConfigMapForm(player, map);
	}

	private void handleConfigMapForm(Player player, FormResponseSimple response)
	{
		String level = editLevel.get(player.getUniqueId());
		MRArenasConfig mapConfig = main.getMapConfigs().get(level);

		if(response.getClickedButtonId() == 6)
		{
			if(main.correctMapAreasConfig(mapConfig))
			mapConfig.toggleMapEnabled();
			else
				player.sendMessage(ConfigLang.FAILCONFIG.toString());
		}

		else if(!mapConfig.isMapEnabled())
		{
			switch(response.getClickedButtonId())
			{
			case 0:
				player.switchLevel(mapConfig.getOriginalMapLevel());
				break;
			case 1:
				addSettingsMapForm(player, level);
				break;
			case 2:
				mapConfig.setSurvivorLocation(player);
				player.sendMessage(ConfigLang.SLOCATIONMESSAGE.toString());
				break;
			case 3:
				mapConfig.setMurdererLocation(player);
				player.sendMessage(ConfigLang.MLOCATIONMESSAGE.toString());
				break;
			case 4:
				mapConfig.setGameEndLocation(player);
				player.sendMessage(ConfigLang.GLOCATIONMESSAGE.toString());
				break;
			case 5:
				mapConfig.addVehicleLocation(player);
				player.sendMessage(ConfigLang.VLOCATIONMESSAGE.toString());
				break;
			}
		} else
			player.sendMessage(ConfigLang.DISABLEMAP.toString());

		if(mapConfig.isMapEnabled() || response.getClickedButtonId() > 1)
			addConfigMapForm(player, level);
	}

	private void handleMapsSelector(Player player, FormResponseSimple response, MapModes type)
	{
		String map = idMap.get(player).get(response.getClickedButtonId());
		MRArenasConfig config = main.getMapConfigs().get(map);
		int normal = config.getMultiples(type);

		if(main.getMapConfigs().get(map).isMapEnabled())
			for(int i = 1; i <= normal; i++)
			{
				MRTeam team = MRTeam.getMapTeamByID(map, i, type);
				if(team.getState() == MapState.READY || team.getState() == MapState.STARTING)
				{
					PlayerJoinGameEvent join = new PlayerJoinGameEvent(player, team);
					player.sendMessage(util.formatLevel(MRMain.getPrefix()+" "+ConfigLang.SUCCESSJOIN.toString(), map));
					Server.getInstance().getPluginManager().callEvent(join);
					break;
				}
			}

		if(MRPlayer.getMRPlayer(player) == null)
			player.sendMessage(util.format(ConfigLang.NOTAVAILABLEMAP.toString()));

		idMap.remove(player);
	}
}
