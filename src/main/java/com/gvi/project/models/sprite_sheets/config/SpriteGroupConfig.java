package com.gvi.project.models.sprite_sheets.config;

import com.gvi.project.models.core.Config;

import java.util.Map;

public class SpriteGroupConfig extends Config {
	public String spriteGroupId;
	public Map<String, SpriteConfig> sprites;

	public SpriteGroupConfig() {}
}
