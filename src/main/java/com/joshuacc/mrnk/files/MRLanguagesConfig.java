package com.joshuacc.mrnk.files;

import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;

public class MRLanguagesConfig extends AbstractFiles {

	public MRLanguagesConfig(MRMain main) {
		super(main, "MRLanguagesConfig");
	}

	@Override
	public void addDefaults() 
	{
		for(ConfigLang lang: ConfigLang.values())
			addDefault(lang.getKey(), lang.getValue());
		ConfigLang.setLines(config);
	}
}
