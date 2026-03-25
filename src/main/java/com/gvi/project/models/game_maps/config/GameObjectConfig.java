package com.gvi.project.models.game_maps.config;

import com.gvi.project.models.data_objects.ConditionsObject;

import java.util.Map;

public class GameObjectConfig {
	public String objectType;
	public String id;
	public int x;
	public int y;
	public ConditionsObject conditions;
	public Map<String, String> data;

	public GameObjectConfig() {}
}
