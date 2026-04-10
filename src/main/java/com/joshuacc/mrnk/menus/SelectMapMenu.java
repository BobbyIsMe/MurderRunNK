package com.joshuacc.mrnk.menus;

import com.joshuacc.mrnk.files.MRArenasConfig;
import com.joshuacc.mrnk.items.ItemHelper;
import com.joshuacc.mrnk.items.SelectMapItem;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam;
import com.joshuacc.mrnk.main.MRTeam.MapModes;
import com.joshuacc.mrnk.utils.MapState;
import com.joshuacc.mrnk.utils.TextUtils;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public class SelectMapMenu extends FormMenu
{
	private final MapModes mapMode;
	private final MRMain main;
	public SelectMapMenu(MapModes mapMode) 
	{
		super(mapMode.getID());
		this.mapMode = mapMode;
		this.main = MRMain.getInstance();
	}

	@Override
	public FormWindow createForm(Player player) 
	{
		FormWindowSimple mapSelector = new FormWindowSimple(mapMode.getTitle(), mapMode.getDesc());
		for(String map : main.getMaps())
		{
			MRArenasConfig config = main.getMapConfigs().get(map);

			if(config.isMapEnabled())
			{
				int size = config.getMultiples(mapMode.getMode());

				if(size != 0)
				{
					int all = 0;

					for(int i = 1; i <= size; i++)
					{
						MapState mode = MRTeam.getMapTeamByID(map, i, mapMode).getState();
						if(mode == MapState.READY || mode == MapState.STARTING)
							all++;
					}
					
					mapSelector.addButton(new SelectMapItem(TextUtils.formatLevel(FormsLang.SELMAPNAME.toString(), map)+"\n"+TextUtils.formatNumber(FormsLang.SELMAPNUM.toString(), all), map, mapMode));
				}
			}
		}
		return mapSelector;
	}

	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseSimple r = (FormResponseSimple) response;
		if(r == null)
			return;
		
		ElementButton button = r.getClickedButton();
		if(button instanceof ItemHelper)
		{
			((ItemHelper) button).itemResponse(player);
		}
	}

}
