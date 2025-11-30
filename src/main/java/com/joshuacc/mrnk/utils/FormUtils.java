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
import com.joshuacc.mrnk.menus.FormMenu;
import com.joshuacc.mrnk.menus.FormMenu.GameMenus;

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
import cn.nukkit.level.Position;

public class FormUtils {

	private MRMain main;
	private HashMap<UUID,String> editLevel = new HashMap<>();
	private HashMap<Integer,String> maps;
	private HashMap<Player, HashMap<Integer,String>> idMap = new HashMap<>();
	private static final int CONFIG_OFFSET = 4;

	public FormUtils(MRMain main)
	{
		this.main = main;
		initializeMaps();
		GameMenus.values();
	}
	
	public void initializeMaps()
	{
		maps = new HashMap<>();
		if(main.getMaps() != null)
			for(int i = 0; i < main.getMaps().length; i++)
				maps.put(i+CONFIG_OFFSET, main.getMaps()[i]);
		else
			maps = null;
	}

	public void addConfigMenu(Player player)
	{
		if(editLevel.containsKey(player.getUniqueId()))
		{
			addConfigMapForm(player, editLevel.get(player.getUniqueId()));
			return;
		}
		FormWindowSimple configMenu = new FormWindowSimple(FormsLang.CONTITLE.toString(), FormsLang.CONDESC.toString());
		configMenu.addButton(new ElementButton(FormsLang.CONTPLOBBY.toString()));
		configMenu.addButton(new ElementButton(FormsLang.CONTPQLOBBY.toString()));
		configMenu.addButton(new ElementButton(FormsLang.CONLOBBY.toString()));
		configMenu.addButton(new ElementButton(FormsLang.CONWLOBBY.toString()));
		if(maps != null)
		{
			for (String maps : maps.values()) {
				configMenu.addButton(new ElementButton(TextUtils.formatLevel(FormsLang.CONTYPE.toString(), maps)));
			}
		player.showFormWindow(configMenu, 100);
		}
		else
			player.sendMessage(FormsLang.CONNOMAPS.toString());
	}

	public void addConfigMapForm(Player player, String level)
	{
		FormWindowSimple configForm = new FormWindowSimple(TextUtils.formatLevel(FormsLang.EDITTITLE.toString(), level), FormsLang.EDITDESC.toString());
		configForm.addButton(new ElementButton(FormsLang.EDITEXIT.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITTEL.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITMAP.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITSLOC.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITMLOC.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITGLOC.toString()));
		configForm.addButton(new ElementButton(FormsLang.EDITVLOC.toString()));

		if(main.getMapConfigs().get(level).isMapEnabled())
			configForm.addButton(new ElementButton("§4§l"+FormsLang.EDITENABLE.toString(), new ElementButtonImageData("url", "https://icons.iconarchive.com/icons/paomedia/small-n-flat/128/sign-error-icon.png")));
		else
			configForm.addButton(new ElementButton("§2§l"+FormsLang.EDITENABLE.toString(), new ElementButtonImageData("url", "https://icons.iconarchive.com/icons/paomedia/small-n-flat/128/sign-check-icon.png")));

		player.showFormWindow(configForm);
	}

	public void addSettingsMapForm(Player player, String level)
	{
		MRArenasConfig config = main.getMapConfigs().get(editLevel.get(player.getUniqueId()));
		FormWindowCustom editForm = new FormWindowCustom(TextUtils.formatLevel(FormsLang.SNGTITLE.toString(), level));
		editForm.addElement(new ElementLabel(FormsLang.SNGDESC.toString()));

		for(MapModes modes : MapModes.values())
			editForm.addElement(new ElementInput(modes.getMapMultiples(), modes.getMode().toLowerCase(), config.getInt(modes.getMode()+" Multiples")));

		editForm.addElement(new ElementInput(FormsLang.SNGPREP.toString(), "prepare", config.getInt("Preparing Time")));
		editForm.addElement(new ElementInput(FormsLang.SNGHIDE.toString(), "hiding time", config.getInt("Hiding Time")));
		editForm.addElement(new ElementInput(FormsLang.SNGYLEVELTIME.toString(), "y level time", config.getInt("Y Level Time")));
		editForm.addElement(new ElementInput(FormsLang.SNGYLEVELSTART.toString(), "y level start", config.getInt("Y Level Start")));
		editForm.addElement(new ElementInput(FormsLang.SNGYLEVELEND.toString(), "y level end", config.getInt("Y Level End")));
		editForm.addElement(new ElementInput(FormsLang.SNGYLEVELDECR.toString(), "y level decrement", config.getInt("Y Level Decrement")));
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
						mapSelector.addButton(new ElementButton(TextUtils.formatLevel(FormsLang.SELMAPNAME.toString(), maps)+"\n"+TextUtils.formatNumber(FormsLang.SELMAPNUM.toString(), all)));
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
		FormMenu menu = FormMenu.getFormMenu(id);
		if(FormMenu.getFormMenu(id) != null)
		{
			menu.response(player, response);
			return;
		}
		
		if(window.wasClosed())
		{
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
		}
		
		for(MapModes mode : MapModes.values())
			if(id == mode.getID())
			{
				handleMapsSelector(player, (FormResponseSimple) response, mode);
				return;
			}

		if(editLevel.get(player.getUniqueId()) != null)
			handleConfigMapForm(player, (FormResponseSimple) response);
	}

	private void handleConfigMenuForm(Player player, FormResponseSimple response)
	{
		int id = response.getClickedButtonId();
		MRLobbyConfig lobby = main.getMRLobbyConfig();
		switch(id)
		{
		case 0:
			player.teleport(main.getMRLobbyConfig().getMainLobbyLocation());
			break;
		case 1:
			player.teleport(main.getMRLobbyConfig().getQueueLobbyLocation());
			break;
		case 2:
			lobby.setupLobbyLocation(player, true);
			player.sendMessage(ConfigLang.SUCCESSLOBBY.toString());
			break;
		case 3:
			lobby.setupLobbyLocation(player, false);
			player.sendMessage(ConfigLang.SUCCESSWLOBBY.toString());
			break;
		default:

			if(checkLobbySpawns(player))
				return;

			String level = maps.get(id);
			editLevel.put(player.getUniqueId(), level);
			addConfigMapForm(player, level);
			break;
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
			int i = 1;

			for(MapModes modes : MapModes.values())
			{
				config.setValue(modes.getMode()+" Multiples", response.getInputResponse(i));
				i++;
			}

			config.setValue("Preparing Time", response.getInputResponse(i++));
			config.setValue("Hiding Time", response.getInputResponse(i++));
			config.setValue("Y Level Time", response.getInputResponse(i++));
			config.setValue("Y Level Start", response.getInputResponse(i++));
			config.setValue("Y Level End", response.getInputResponse(i++));
			config.setValue("Y Level Decrement", response.getInputResponse(i++));
			config.setValue("Time Limit", response.getInputResponse(i++));
			config.setValue("Points Limit", response.getInputResponse(i++));
			config.setValue("Minimum Players", response.getInputResponse(i++));
			config.setValue("Maximum Players", response.getInputResponse(i++));
			config.getConfig().save();
			player.sendMessage(TextUtils.formatLevel(ConfigLang.CHANGESETTINGS.toString(), map));
		} catch(Exception e) {
			player.sendMessage(ConfigLang.NOTNUMBER.toString());
		}

		addConfigMapForm(player, map);
	}

	private void handleConfigMapForm(Player player, FormResponseSimple response)
	{
		String level = editLevel.get(player.getUniqueId());
		MRArenasConfig mapConfig = main.getMapConfigs().get(level);

		if(response.getClickedButtonId() == 7)
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
				editLevel.remove(player.getUniqueId());
				if(player.getLevel().getFolderName().equals(mapConfig.getOriginalMapLevel().getFolderName()))
				player.teleport(main.getMRLobbyConfig().getMainLobbyLocation());
				addConfigMenu(player);
				break;
			case 1:
				Position pos = mapConfig.getOriginalMapLevel().getSpawnLocation();
				Position l = new Position(pos.x, pos.y, pos.z, mapConfig.getOriginalMapLevel());
				player.setLevel(mapConfig.getOriginalMapLevel());
				player.teleport(l);
				break;
			case 2:
				addSettingsMapForm(player, level);
				break;
			case 3:
				mapConfig.setSurvivorLocation(player);
				player.sendMessage(ConfigLang.SLOCATIONMESSAGE.toString());
				break;
			case 4:
				mapConfig.setMurdererLocation(player);
				player.sendMessage(ConfigLang.MLOCATIONMESSAGE.toString());
				break;
			case 5:
				mapConfig.setGameEndLocation(player);
				player.sendMessage(ConfigLang.GLOCATIONMESSAGE.toString());
				break;
			case 6:
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

		if(main.getMapConfigs().get(map).isMapEnabled() && MRPlayer.getMRPlayer(player) == null)
			for(int i = 1; i <= normal; i++)
			{
				MRTeam team = MRTeam.getMapTeamByID(map, i, type);
				if(team.getState() == MapState.READY || team.getState() == MapState.STARTING)
				{
					PlayerJoinGameEvent join = new PlayerJoinGameEvent(player, team);
					player.sendMessage(TextUtils.format(TextUtils.formatLevel(ConfigLang.SUCCESSJOIN.toString(), map)));
					Server.getInstance().getPluginManager().callEvent(join);
					break;
				}
			}

		if(MRPlayer.getMRPlayer(player) == null)
			player.sendMessage(TextUtils.format(ConfigLang.NOTAVAILABLEMAP.toString()));

		idMap.remove(player);
	}
}
