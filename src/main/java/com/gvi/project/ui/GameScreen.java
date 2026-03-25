package com.gvi.project.ui;

import com.gvi.project.GeneralSettings;

public abstract class GameScreen {
	protected final int screenWidth;
	protected final int screenHeight;

	public GameScreen(){
		this.screenWidth = GeneralSettings.getScreenWidth();
		this.screenHeight = GeneralSettings.getScreenHeight();
	}
}
