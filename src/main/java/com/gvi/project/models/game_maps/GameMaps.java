package com.gvi.project.models.game_maps;

public enum GameMaps {
	MAP_00("map_00.json", 0),
	MAP_01("map_01.json", 1),
	MAP_02("map_02.json", 2),
	MAP_03("map_03.json", 3);

	private final String configFileName;
	private final int id;

	GameMaps(String configFileName, int id){
		this.configFileName = configFileName;
		this.id = id;
	}

	public String getConfigFileName(){
		return configFileName;
	}

	public static GameMaps fromId(int id) {
		for (GameMaps map : values()) {
			if (map.id == id) return map;
		}
		throw new IllegalArgumentException("Unknown map id: " + id);
	}
}
