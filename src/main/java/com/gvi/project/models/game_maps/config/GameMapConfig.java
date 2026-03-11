package com.gvi.project.models.game_maps.config;

import com.gvi.project.models.core.Config;

public class GameMapConfig extends Config {
	public int id;
	public String name;
	public int width, height;
	public GameMapLayerConfig[] layers;
	public GameObjectConfig[] objects;

	public GameMapConfig() {}
}
