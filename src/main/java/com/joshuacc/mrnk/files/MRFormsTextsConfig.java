package com.joshuacc.mrnk.files;

import com.joshuacc.mrnk.lang.FormsLang;
import com.joshuacc.mrnk.main.MRMain;

public class MRFormsTextsConfig extends AbstractFiles {

	public MRFormsTextsConfig(MRMain main) 
	{
		super(main, "MRFormsTextsConfig");
	}

	@Override
	public void addDefaults() 
	{
		for(FormsLang lang: FormsLang.values())
			addDefault(lang.getKey(), lang.getValue());
		FormsLang.setLines(config);
	}
}
