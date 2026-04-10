package com.joshuacc.mrnk.menus.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import com.joshuacc.mrnk.files.MRLobbyConfig;
import com.joshuacc.mrnk.items.ItemHelper;
import com.joshuacc.mrnk.items.MapItem;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.menus.FormMenu;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public class ConfigMenu extends FormMenu {

	private final HashMap<UUID, EditMap> editLevel;
	private final ArrayList<MapItem> mapItems;
	
	private final ItemHelper tpLobbyButton;
	private final ItemHelper tpQLobbyButton;
	private final ItemHelper lobbyButton;
	private final ItemHelper winLobbyButton;
	
	private MRMain main;
	
	public ConfigMenu(int id) 
	{
		super(id);
		main = MRMain.getInstance();
		MRLobbyConfig lobby = main.getMRLobbyConfig();
		tpLobbyButton = new ConfigItem(FormsLang.CONTPLOBBY.toString(), player -> {
			player.teleport(main.getMRLobbyConfig().getMainLobbyLocation());
		});
		
		tpQLobbyButton = new ConfigItem(FormsLang.CONTPQLOBBY.toString(), player -> {
			player.teleport(main.getMRLobbyConfig().getQueueLobbyLocation());
		});
		
		lobbyButton = new ConfigItem(FormsLang.CONLOBBY.toString(), player -> {
			lobby.setupLobbyLocation(player, true);
			player.sendMessage(ConfigLang.SUCCESSLOBBY.toString());
		});
		
		winLobbyButton = new ConfigItem(FormsLang.CONWLOBBY.toString(), player -> {
			lobby.setupLobbyLocation(player, false);
			player.sendMessage(ConfigLang.SUCCESSWLOBBY.toString());
		});
		
		editLevel = new HashMap<>();
		mapItems = new ArrayList<>();
		String[] maps = MRMain.getInstance().getMaps();
		if(maps != null)
			for(String map : maps)
				mapItems.add(new MapItem(TextUtils.formatLevel(FormsLang.CONTYPE.toString(), map), map));
		else
			maps = null;
	}
	
	public void openRemove(Player player)
	{
		editLevel.remove(player.getUniqueId());
		open(player);
	}
	
	public String getMap(Player player)
	{
		return editLevel.get(player.getUniqueId()).getMap();
	}
	
	public MapModes getMode(Player player)
	{
		return editLevel.get(player.getUniqueId()).getMode();
	}
	
	public ConfigMapModeMenu getMapModeMenu(Player player)
	{
		return editLevel.get(player.getUniqueId()).getMenu();
	}
	
	public void setMode(Player player, ConfigMapModeMenu menu)
	{
		editLevel.get(player.getUniqueId()).setMapModeMenu(menu);
	}

	@Override
	public FormWindow createForm(Player player) 
	{
		if(editLevel.containsKey(player.getUniqueId())) 
		{
			ConfigMapModeMenu menu = editLevel.get(player.getUniqueId()).getMenu();
			if(menu != null)
				menu.open(player);
			else
				GameMenus.CONFIGMAPMENU.getFormMenu().open(player);
			return null;
		}
		
		FormWindowSimple configMenu = new FormWindowSimple(FormsLang.CONTITLE.toString(), FormsLang.CONDESC.toString());
		configMenu.addButton(tpLobbyButton);
		configMenu.addButton(tpQLobbyButton);
		configMenu.addButton(lobbyButton);
		configMenu.addButton(winLobbyButton);
		
		for(MapItem item : mapItems)
		{
			configMenu.addButton(item);
		}
		
		return configMenu;
	}

	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseSimple r = (FormResponseSimple) response;
		if(r == null)
			return;
		
		if(r.getClickedButton() instanceof MapItem)
		{
			MRLobbyConfig lobby = main.getMRLobbyConfig();
			if(lobby.getMainLobbyLocation().getLevel() == null)
			{
				player.sendMessage(ConfigLang.NOLOBBYSPAWN.toString());
				return;
			}

			if(lobby.getQueueLobbyLocation().getLevel() == null)
			{
				player.sendMessage(ConfigLang.NOLOBBYQUEUESPAWN.toString());
				return;
			}
			editLevel.put(player.getUniqueId(), new EditMap(((MapItem) r.getClickedButton()).getMap()));
		}
		
		if(r.getClickedButton() instanceof ItemHelper)
		{
			((ItemHelper) r.getClickedButton()).itemResponse(player);
		}
	}

}

class EditMap
{
	private final String map;
	private ConfigMapModeMenu menu;
	
	public EditMap(String map)
	{
		this.map = map;
		menu = null;
	}
	
	public String getMap()
	{
		return map;
	}
	
	public MapModes getMode()
	{
		return menu.getMapMode();
	}
	
	public ConfigMapModeMenu getMenu()
	{
		return menu;
	}
	
	public void setMapModeMenu(ConfigMapModeMenu menu)
	{
		this.menu = menu;
	}

}

class ConfigItem extends ItemHelper {

    private final Consumer<Player> action;

    public ConfigItem(String text, Consumer<Player> action) {
        super(text);
        this.action = action;
    }
    
    public ConfigItem(String text, ElementButtonImageData image, Consumer<Player> action) {
        super(text, image);
        this.action = action;
    }

    public void itemResponse(Player player) {
        if (action != null) {
            action.accept(player);
        }
    }
}
