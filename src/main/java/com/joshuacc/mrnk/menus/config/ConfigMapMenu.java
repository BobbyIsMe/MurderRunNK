package com.joshuacc.mrnk.menus.config;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.items.ItemHelper;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

public class ConfigMapMenu extends ConfigBackMenu 
{
	
	public ConfigMapMenu(int id) 
	{
		super(id);
	}
	
	@Override
	public void backMenu(Player player) 
	{
		String map = ((ConfigMenu) GameMenus.CONFIGMENU.getFormMenu()).getMap(player);
		MRMain main = MRMain.getInstance();
		MRArenasConfig mapConfig = main.getMapConfigs().get(map);
		
		if(player.getLevel().getFolderName().equals(mapConfig.getOriginalMapLevel().getFolderName()))
			player.teleport(main.getMRLobbyConfig().getMainLobbyLocation());
		
		((ConfigMenu) GameMenus.CONFIGMENU.getFormMenu()).openRemove(player);
	}

	@Override
	public String getTitle(Player player) 
	{
		return TextUtils.formatLevel(FormsLang.EDITTITLE.toString(), ((ConfigMenu) GameMenus.CONFIGMENU.getFormMenu()).getMap(player));
	}

	@Override
	public String getDesc() 
	{
		return FormsLang.EDITDESC.toString();
	}

	@Override
	public void addItems(Player player, FormWindowSimple form) 
	{
		String map = ((ConfigMenu) GameMenus.CONFIGMENU.getFormMenu()).getMap(player);
		MRArenasConfig mapConfig = MRMain.getInstance().getMapConfigs().get(map);
		ItemHelper editTp = new ItemHelper(FormsLang.EDITTEL.toString()) {

			@Override
			public void itemResponse(Player player) 
			{
				Position pos = mapConfig.getOriginalMapLevel().getSpawnLocation();
				Position l = new Position(pos.x, pos.y, pos.z, mapConfig.getOriginalMapLevel());
				player.setLevel(mapConfig.getOriginalMapLevel());
				player.teleport(l);
			}
			
		};
		ItemHelper editMap = new ItemHelper(FormsLang.EDITMODES.toString()) {

			@Override
			public void itemResponse(Player player) 
			{
				GameMenus.CONFIGMODESMENU.getFormMenu().open(player);
			}
			
		};
		ItemHelper editSurvLoc = new ItemHelper(FormsLang.EDITEXIT.toString()) {

			@Override
			public void itemResponse(Player player) 
			{
				mapConfig.setSurvivorLocation(player);
				player.sendMessage(ConfigLang.SLOCATIONMESSAGE.toString());
			}
			
		};
		ItemHelper editMurdLoc = new ItemHelper(FormsLang.EDITMLOC.toString()) {

			@Override
			public void itemResponse(Player player) 
			{
				mapConfig.setMurdererLocation(player);
				player.sendMessage(ConfigLang.MLOCATIONMESSAGE.toString());
			}
			
		};
		ItemHelper editGameEndLoc = new ItemHelper(FormsLang.EDITGLOC.toString()) {

			@Override
			public void itemResponse(Player player) 
			{
				mapConfig.setGameEndLocation(player);
				player.sendMessage(ConfigLang.GLOCATIONMESSAGE.toString());
			}
			
		};
		
		form.addButton(editTp);
		form.addButton(editMap);
		form.addButton(editSurvLoc);
		form.addButton(editMurdLoc);
		form.addButton(editGameEndLoc);
		
		form.addButton(new ItemHelper(
				TextFormat.colorize((mapConfig.isMapEnabled() ? "&4&l" : "&2&l") + FormsLang.EDITENABLE.toString()),
				new ElementButtonImageData("url",
						mapConfig.isMapEnabled() ? "https://icons.iconarchive.com/icons/paomedia/small-n-flat/128/sign-error-icon.png" : "https://icons.iconarchive.com/icons/paomedia/small-n-flat/128/sign-error-icon.png")) {

			@Override
			public void itemResponse(Player player) {
				if (MRMain.getInstance().correctMapAreasConfig(mapConfig))
					mapConfig.toggleMapEnabled();
				else
					player.sendMessage(ConfigLang.FAILCONFIG.toString());
			}

		});

	}

	@Override
	public void menuResponse(Player player, FormResponseSimple response) 
	{
		
	}

}
