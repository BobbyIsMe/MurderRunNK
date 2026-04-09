package com.joshuacc.mrnk.menus.config;

import java.util.ArrayList;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.items.ItemHelper;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.menus.FormMenu;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;

public abstract class ConfigMapSettingsMenu extends FormMenu
{

	private ArrayList<ItemHelper> items;
	private MRMain main;
	
	public ConfigMapSettingsMenu(int id) 
	{
		super(id);
		items = new ArrayList<ItemHelper>();
		main = MRMain.getInstance();
	}
	
	public void registerItem(ItemHelper item)
	{
		items.add(item);
	}
	
	public abstract void inputResponse(MRArenasConfig config, FormResponseCustom response, int i);

	@Override
	public FormWindow createForm(Player player)
	{
		ConfigMenu configMenu = (ConfigMenu) GameMenus.CONFIGMENU.getFormMenu();
		String map = configMenu.getMap(player);
		MapModes mapMode = configMenu.getMode(player);
		String mode = mapMode.getMode();
		FormWindowCustom editForm = new FormWindowCustom(TextUtils.formatLevel(FormsLang.SNGTITLE.toString(), map).replace("%s", configMenu.getMode(player).getMode()));
		MRArenasConfig config = main.getMapConfigs().get(map);
		editForm.addElement(new ElementLabel(FormsLang.SNGDESC.toString()));
		
		editForm.addElement(new ElementInput(mapMode.getMapMultiples(),mapMode.getMode().toLowerCase(), config.getIntFromMode(mode, "Multiples")));
		editForm.addElement(new ElementInput(FormsLang.SNGPREP.toString(), "prepare", config.getIntFromMode(mode, "Preparing Time")));
		editForm.addElement(new ElementInput(FormsLang.SNGHIDE.toString(), "hiding time", config.getIntFromMode(mode, "Hiding Time")));
		editForm.addElement(new ElementInput(FormsLang.SNGYLEVELTIME.toString(), "y level time", config.getIntFromMode(mode, "Y Level Time")));
		editForm.addElement(new ElementInput(FormsLang.SNGYLEVELSTART.toString(), "y level start", config.getIntFromMode(mode, "Y Level Start")));
		editForm.addElement(new ElementInput(FormsLang.SNGYLEVELEND.toString(), "y level end", config.getIntFromMode(mode, "Y Level End")));
		editForm.addElement(new ElementInput(FormsLang.SNGYLEVELDECR.toString(), "y level decrement", config.getIntFromMode(mode, "Y Level Decrement")));
		editForm.addElement(new ElementInput(FormsLang.SNGTIME.toString(), "time", config.getIntFromMode(mode, "Time Limit")));
		editForm.addElement(new ElementInput(FormsLang.SNGPOINTS.toString(), "points", config.getIntFromMode(mode, "Points Limit")));
		editForm.addElement(new ElementInput(FormsLang.SNGMINI.toString(), "mini", config.getIntFromMode(mode, "Minimum Players")));
		editForm.addElement(new ElementInput(FormsLang.SNGMAX.toString(), "max", config.getIntFromMode(mode, "Maximum Players")));
		
		return editForm;
	}
	
	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseCustom r = (FormResponseCustom) response;
		ConfigMenu editMap = (ConfigMenu) GameMenus.CONFIGMENU.getFormMenu();
		String map = editMap.getMap(player);
		MapModes mapMode = editMap.getMode(player);
		if(r == null || map ==  null || mapMode == null)
		{
			return;
		}
		
		String mode = mapMode.getMode();
		
		try {
			MRArenasConfig config = main.getMapConfigs().get(map);
			int i = 1;
			config.setModeConfigValue(mode, "Multiples", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Preparing Time", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Hiding Time", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Y Level Time", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Y Level Start", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Y Level End", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Y Level Decrement", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Time Limit", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Points Limit", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Minimum Players", r.getInputResponse(i++));
			config.setModeConfigValue(mode, "Maximum Players", r.getInputResponse(i++));
			inputResponse(config, r, i);
			config.getConfig().save();
			player.sendMessage(TextUtils.formatLevel(ConfigLang.CHANGESETTINGS.toString(), map));
		} catch(Exception e) {
			player.sendMessage(ConfigLang.NOTNUMBER.toString());
		}
		
		this.open(player);
	}

}
