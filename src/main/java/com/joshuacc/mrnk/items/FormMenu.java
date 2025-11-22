package com.joshuacc.mrnk.items;

import java.util.HashMap;

import com.joshuacc.mrnk.lang.FormsLang;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;

public abstract class FormMenu {
	
	public enum GameMenus {
		SURVITEMSMENU(null), //TODO: add this
		SELITEMMENU(new SelectItemMenu(300)),
		ARMORMENU(new ArmorMenu(301));
		
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
		player.showFormWindow(createForm(player), id);
	}
	
	public void open(FormWindow window, Player player)
	{
		player.showFormWindow(window, id);
	}
}
