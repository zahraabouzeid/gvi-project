package com.gvi.project.models.game_maps;

public enum GameMaps {
	MAP_01("map_01.json"),
	MAP_02("map_02.json"),
	MAP_03("map_03.json"),
	MAP_00("map_00.json");

	private String configFileName;

	GameMaps(String configFileName){
		this.configFileName = configFileName;
	}

	public String getConfigFileName(){
		return configFileName;
	}
}
