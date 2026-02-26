package com.gvi.project.models.game_levels;

public enum GameLevels {
	LEVEL_1("map_1.json"),
	LEVEL_2("map_2.json"),
	LEVEL_3("map_3.json"),
	TEST_LEVEL("mock_map_01.json");

	private String configFileName;

	GameLevels(String configFileName){
		this.configFileName = configFileName;
	}

	public String getConfigFileName(){
		return configFileName;
	}
}
