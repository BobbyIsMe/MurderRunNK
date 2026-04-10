package com.joshuacc.mrnk.menus.config;

import java.util.function.Function;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

public class ConfigMapMenu extends ConfigBackMenu 
{
	private final ConfigItem editTp;
	private final ConfigItem editMap;
	private final ConfigItem editSurvLoc;
	private final ConfigItem editMurdLoc;
	private final ConfigItem editGameEndLoc;
	
	public ConfigMapMenu(int id) 
	{
		super(id);
		Function<Player, MRArenasConfig> func = player -> MRMain.getInstance().getMapConfigs()
				.get(((ConfigMenu) GameMenus.CONFIGMENU.getFormMenu()).getMap(player));
		
		editTp = new ConfigItem(FormsLang.EDITTEL.toString(), player -> {
			MRArenasConfig mapConfig = func.apply(player);
			Position pos = mapConfig.getOriginalMapLevel().getSpawnLocation();
			Position l = new Position(pos.x, pos.y, pos.z, mapConfig.getOriginalMapLevel());
			player.setLevel(mapConfig.getOriginalMapLevel());
			player.teleport(l);
			player.setGamemode(1);
			player.getAdventureSettings().set(Type.FLYING, true);
			player.getAdventureSettings().update();
		});
		
		editMap = new ConfigItem(FormsLang.EDITMODES.toString(), player -> {
			GameMenus.CONFIGMODESMENU.getFormMenu().open(player);
		});
		
		editSurvLoc = new ConfigItem(FormsLang.EDITSLOC.toString(), player -> {
			func.apply(player).setSurvivorLocation(player);
			player.sendMessage(ConfigLang.SLOCATIONMESSAGE.toString());
		});
		
		editMurdLoc = new ConfigItem(FormsLang.EDITMLOC.toString(), player -> {
			func.apply(player).setMurdererLocation(player);
			player.sendMessage(ConfigLang.MLOCATIONMESSAGE.toString());
		});
		
		editGameEndLoc = new ConfigItem(FormsLang.EDITGLOC.toString(), player -> {
			func.apply(player).setGameEndLocation(player);
			player.sendMessage(ConfigLang.GLOCATIONMESSAGE.toString());
		});
	}
	
	@Override
	public void backMenu(Player player) 
	{
		String map = ((ConfigMenu) GameMenus.CONFIGMENU.getFormMenu()).getMap(player);
		MRMain main = MRMain.getInstance();
		MRArenasConfig mapConfig = main.getMapConfigs().get(map);
		
		if(mapConfig.getOriginalMapLevel() != null && player.getLevel().getFolderName().equals(mapConfig.getOriginalMapLevel().getFolderName()))
		{
			player.teleport(main.getMRLobbyConfig().getMainLobbyLocation());
			player.setGamemode(0);
		}
		
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
		
		
		form.addButton(editTp);
		form.addButton(editMap);
		form.addButton(editSurvLoc);
		form.addButton(editMurdLoc);
		form.addButton(editGameEndLoc);
		
		form.addButton(new ConfigItem(
				TextFormat.colorize((mapConfig.isMapEnabled() ? "&4&l" : "&2&l") + FormsLang.EDITENABLE.toString()),
				new ElementButtonImageData("url",
						mapConfig.isMapEnabled() ? "https://icons.iconarchive.com/icons/paomedia/small-n-flat/128/sign-error-icon.png" : "https://icons.iconarchive.com/icons/paomedia/small-n-flat/128/sign-check-icon.png"), p -> {
							if (MRMain.getInstance().correctMapAreasConfig(mapConfig))
								mapConfig.toggleMapEnabled();
							else
								player.sendMessage(ConfigLang.FAILCONFIG.toString());
						}));

	}

	@Override
	public void menuResponse(Player player, FormResponseSimple response) 
	{
		
	}

}
