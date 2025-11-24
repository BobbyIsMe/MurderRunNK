package com.joshuacc.mrnk.menus;

import java.util.ArrayList;
import java.util.HashMap;

import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRItemFilters;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;

public class FilterItemsMenu extends FormMenu {
	
	private HashMap<Player, ItemMenu> menus;
	public FilterItemsMenu(int id) 
	{
		super(id);
		menus = new HashMap<>();
	}
	
	public void setItemFilterMenu(Player player, ItemMenu menu)
	{
		menus.put(player, menu);
	}

	@Override
	public FormWindow createForm(Player player) 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(mPlayer == null)
			return null;

		ArrayList<String> sortPrice = new ArrayList<>();
		ArrayList<String> sortType = new ArrayList<>();
		
		sortPrice.add(FormsLang.LOWTOHIGH.toString());
		sortPrice.add(FormsLang.HIGHTOLOW.toString());
		
		sortType.add(FormsLang.ALLCATEGORY.toString());
		if (menus.get(player) instanceof SurvivorTrapMenu) {
			sortType.add(FormsLang.TRAPCLICK.toString());
			sortType.add(FormsLang.TRAPDROP.toString());
		}
		
		MRItemFilters filter = mPlayer.getItemFilter(menus.get(player));
		FormWindowCustom form = new FormWindowCustom(FormsLang.FILTERTITLE.toString());
		form.addElement(new ElementLabel(FormsLang.FILTERDESC.toString()));
		form.addElement(new ElementInput(FormsLang.SEARCHKEYWORD.toString(), "", filter.getKeyword()));
		form.addElement(new ElementDropdown(FormsLang.SORTPRICE.toString(), sortPrice, sortPrice.indexOf(filter.getPrice())));
		form.addElement(new ElementDropdown(FormsLang.SORTCATEGORY.toString(), sortType, sortType.indexOf(filter.getType())));
		return form;
	}

	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseCustom r = (FormResponseCustom) response;
		if(r == null || MRPlayer.getMRPlayer(player) == null || menus.get(player) == null)
		{
			if(menus.get(player) != null && MRPlayer.getMRPlayer(player) != null)
				menus.remove(player).open(player);
			return;
		}
		
		ItemMenu menu = menus.get(player);
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		MRItemFilters filter = mPlayer.getItemFilter(menu);
		filter.setKeyword(r.getInputResponse(1));
		filter.setPrice(r.getDropdownResponse(2).getElementContent());
		filter.setType(r.getDropdownResponse(3).getElementContent());
		filter.setPage(1);
		menu.getFormMenu().open(player);
		menus.remove(player);
	}
}
