package com.gvi.project.models.game_maps.config;

import com.gvi.project.models.core.Config;

public class GameMapLayerConfig extends Config {
	public String renderId;
	public GameMapSpriteSheetConfig[] usedSpriteSheets;
	public int[][] spriteLayout;

	public GameMapLayerConfig() {}
}
