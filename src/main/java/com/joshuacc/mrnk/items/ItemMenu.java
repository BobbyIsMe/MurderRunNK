package com.joshuacc.mrnk.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRItemFilters;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public abstract class ItemMenu extends FormMenu {

	private static final ElementButton filterButton = new ElementButton(FormsLang.FILTERBUTTON.toString());
	private final ArrayList<ShopItem> items;
	private int page;
	public ItemMenu(int id)
	{
		super(id);
		items = new ArrayList<ShopItem>();
		page = 5;
	}
	
	public void registerItem(ShopItem item)
	{
		items.add(item);
	}
	
	public abstract String getTitle();
	public abstract String getDesc();
	public abstract FormMenu getOrigin();
	
	@Override
	public FormWindow createForm(Player player)
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(mPlayer == null)
			return null;
		
		MRItemFilters filter = mPlayer.getItemFilter(this);
		ArrayList<ShopItem> filteredItems = new ArrayList<>();
		String keyword = filter.getKeyword().toLowerCase();
		
		for(ShopItem item : items)
		{
			if((filter.getType().equals(item.getType())) && (item.getName().toLowerCase().contains(keyword) || item.getDescription().toLowerCase().contains(keyword)))
			{
				Comparator<ShopItem> comparator = (a, b) -> 0;

				if (filter.getPrice().equals(FormsLang.LOWTOHIGH.toString()))
					comparator = comparator.thenComparingInt(ShopItem::getPrice);
				else if (filter.getPrice().equals(FormsLang.HIGHTOLOW.toString()))
					comparator = comparator.thenComparing((x, y) -> Integer.compare(y.getPrice(), x.getPrice()));
				comparator = comparator.thenComparing(ShopItem::getIndex);

				int index = Collections.binarySearch(filteredItems, item, comparator);
				int insertionIndex = index < 0 ? -index - 1 : index;

				filteredItems.add(insertionIndex, item);
			}
		}
		
		int filterPage = filter.getPage();
		filteredItems.subList(page * (filterPage - 1), Math.min(page * filterPage, filteredItems.size()));
		FormWindowSimple menu = new FormWindowSimple(getTitle(), getDesc());
		menu.addElement(backButton);
		menu.addElement(filterButton);
		for(ShopItem item : filteredItems)
		{
			menu.addElement(item);
		}
		return menu;
	}
	
	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseSimple r = (FormResponseSimple) response;
		if(r == null)
			return;
		
		if(r.getClickedButton().getText().equals(backButton.getText()))
		{
			getOrigin().open(player);
			return;
		}
		
		if(r.getClickedButton().getText().equals(filterButton.getText()))
		{
			FilterItemsMenu menu = (FilterItemsMenu) GameMenus.FILTERITEMSMENU.getFormMenu();
			menu.setItemFilterMenu(player, this);
			menu.open(player);
			return;
		}
		
		if(r.getClickedButton() instanceof ShopItem)
		{
			((ShopItem) r.getClickedButton()).itemResponse(player);
		}
	}
}
