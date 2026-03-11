package com.gvi.project.models.game_maps;

public class GameMapLayer {
	public final String[][] layout;

	public GameMapLayer(int width, int height) {
		this.layout = new String[width][height];
	}
}
