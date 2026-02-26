package com.gvi.project.models.game_levels;

import com.gvi.project.models.game_levels.config.GameLevelLayerConfig;
import com.gvi.project.models.tiles.Tile;

import java.util.List;

public class GameLevelLayer {
	private int width;
	private int height;
	private List<Tile> usedTiles;
	private int[][] layout;


	public GameLevelLayer(GameLevelLayerConfig config, int width, int height) {
		this.width = width;
		this.height = height;

		layout = config.spriteLayout;

		loadUsedTiles(config);
	}

	private void loadUsedTiles(GameLevelLayerConfig config){
		config.
	};
}
