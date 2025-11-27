package com.joshuacc.mrnk.menus;

import java.util.HashMap;

import com.joshuacc.mrnk.lang.FormsLang;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;

public abstract class FormMenu {
	
	public enum GameMenus {
		SELITEMMENU(new SelectItemMenu(300)),
		SURVTRAPSMENU(new SurvivorTrapsMenu(301)),
		ARMORMENU(new ArmorMenu(302)),
		UTILMENU(null),
		SURVITEMSMENU(new SurvivorItemsMenu(304)),
		FILTERITEMSMENU(new FilterItemsMenu(305));
		
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
