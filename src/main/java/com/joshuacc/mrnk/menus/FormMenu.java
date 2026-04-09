package com.joshuacc.mrnk.menus;

import java.util.HashMap;

import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.menus.config.ConfigMapMenu;
import com.joshuacc.mrnk.menus.config.ConfigMapNormalModeMenu;
import com.joshuacc.mrnk.menus.config.ConfigMapNormalSettingsMenu;
import com.joshuacc.mrnk.menus.config.ConfigMenu;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;

public abstract class FormMenu {
	
	public enum GameMenus {
		SELECTMAPNORMALMENU(new SelectMapMenu(MapModes.NORMAL)),
		SELECTMAPESCAPEMENU(new SelectMapMenu(MapModes.ESCAPE)),
		SELECTITEMMENU(new SelectItemMenu(300)),
		SELLITEMSMENU(new SellItemsMenu(307)),
		SURVTRAPSMENU(new SurvivorTrapsMenu(301)),
		SURVPERKSMENU(new SurvivorPerksMenu(331)),
		MURDTRAPSMENU(new MurdererTrapsMenu(320)),
		MURDPERKSMENU(new MurdererPerksMenu(332)),
		ARMORMENU(new ArmorMenu(302)),
		UTILMENU(null),
		SURVITEMSMENU(new SurvivorItemsMenu(304)),
		MURDITEMSMENU(new MurdererItemsMenu(321)),
		FILTERITEMSMENU(new FilterItemsMenu(305)),
		SPECTATORMENU(new SpectatorMenu(306)),
		CONFIGMENU(new ConfigMenu(400)),
		CONFIGMAPMENU(new ConfigMapMenu(401)),
		CONFIGMODESMENU(new ConfigMapMenu(402)),
		CONFIGMAPNORMALMODEMENU(new ConfigMapNormalModeMenu(403, MapModes.NORMAL)),
		CONFIGMAPNORMALSETTINGSMENU(new ConfigMapNormalSettingsMenu(404)),;
		
		private FormMenu menu;
		
		GameMenus(FormMenu menu)
		{
			this.menu = menu;
		}
		
		public FormMenu getFormMenu()
		{
			return menu;
		}
	}
	
	private static final HashMap<Integer, FormMenu> formMenus = new HashMap<>();
	protected static final ElementButton backButton = new ElementButton(FormsLang.BACKBUTTON.toString());
	
	private int id;
	
	public FormMenu(int id)
	{
		this.id = id;
		formMenus.put(id, this);
	}
	
	public abstract FormWindow createForm(Player player);
	public abstract void response(Player player, FormResponse response);
	
	public int getId()
	{
		return id;
	}
	
	public static FormMenu getFormMenu(int id)
	{
		return formMenus.get(id);
	}
	
	protected FormMenu getFormMenu()
	{
		return this;
	}
	
	public void open(Player player)
	{
		FormWindow form = createForm(player);
		if(form == null)
			return;
		
		player.showFormWindow(form, id);
	}
	
	public void open(FormWindow window, Player player)
	{
		player.showFormWindow(window, id);
	}
}
