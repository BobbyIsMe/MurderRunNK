package com.joshuacc.mrnk.utils;

import java.io.File;

import org.iq80.leveldb.util.FileUtils;

import com.joshuacc.mrnk.main.MRMain;

public class BackupWorlds {

	private String plugName;
	private MRMain main;

	public BackupWorlds(MRMain main, String plugName)
	{
		this.plugName = plugName;
		this.main = main;
	}
	
	public void copyOverWorld(String levelName, String newName)
	{
		File mapsDirectory = new File(main.getFileDirectory("Maps"), levelName);
		File targetDirectory = new File(main.getFileDirectory(plugName), newName);
		FileUtils.copyDirectoryContents(mapsDirectory, targetDirectory);
	}
}
