package com.gvi.project.models.sprite_sheets.config;

import com.gvi.project.models.core.Config;

import java.util.Map;

public class SpriteSheetConfig extends Config {
	public int spriteSize;
	public Map <String, SpriteGroupConfig> spriteGroups;

	public SpriteSheetConfig(){}
}
