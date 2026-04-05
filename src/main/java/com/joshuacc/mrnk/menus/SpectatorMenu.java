package com.joshuacc.mrnk.menus;

import com.joshuacc.mrnk.items.SpectatorItem;
import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRPlayer;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;

public class SpectatorMenu extends FormMenu {

	public SpectatorMenu(int id) 
	{
		super(id);
	}

	@Override
	public FormWindow createForm(Player player) 
	{
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		FormWindowSimple menu = new FormWindowSimple(FormsLang.SPECTATORTITLE.toString(), FormsLang.SPECTATORDESC.toString());
		if(mPlayer != null)
		{
			for(Player players : mPlayer.getMapTeam().getPlayers())
			{
				menu.addButton(new SpectatorItem(players.getName()));
			}
		}
		return menu;
	}

	@Override
	public void response(Player player, FormResponse response) 
	{
		FormResponseSimple r = (FormResponseSimple) response;
		MRPlayer mPlayer = MRPlayer.getMRPlayer(player);
		if(r == null || mPlayer == null)
			return;
		
		ElementButton button = r.getClickedButton();
		if(r.getClickedButton() instanceof SpectatorItem)
		{
			SpectatorItem item = (SpectatorItem) button;
			
			if(item.getTarget() != null && item.getTarget().isOnline())
				item.itemResponse(player);
		}
	}

}
