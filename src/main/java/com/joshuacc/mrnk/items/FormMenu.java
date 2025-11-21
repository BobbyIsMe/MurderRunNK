package com.joshuacc.mrnk.items;

import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;

public abstract class FormMenu {
	
	public enum GameMenus {
		SURVITEMSMENU(null), //TODO: add this
		ARMORMENU(new ArmorMenu());
		
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
	private static int globalId = 300;
	private static final HashMap<Integer, FormMenu> formMenus = new HashMap<>();
	
	private int id;
	
	public FormMenu()
	{
		this.id = globalId++;
		formMenus.put(id, this);
	}
	
	public abstract FormWindow createForm();
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
		player.showFormWindow(createForm(), id);
	}
	
	public void open(FormWindow window, Player player)
	{
		player.showFormWindow(window, id);
	}
}
