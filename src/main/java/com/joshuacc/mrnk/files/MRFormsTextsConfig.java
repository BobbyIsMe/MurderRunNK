package com.joshuacc.mrnk.files;

import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam.MapModes;

public class MRFormsTextsConfig extends AbstractFiles {

	public MRFormsTextsConfig(MRMain main) 
	{
		super(main, "MRFormsTextsConfig");
	}

	@Override
	public void addDefaults() 
	{
		for(MapModes modes : MapModes.values())
		{
			String mode = modes.getMode();
			addDefault("Map-Selector."+mode+".Title", "&0"+mode+" Mode");
			addDefault("Map-Selector."+mode+".Description", modes.getDescription());
		}
		
		for(FormsLang lang: FormsLang.values())
			addDefault(lang.getKey(), lang.getValue());
		FormsLang.setLines(config);
	}
}
