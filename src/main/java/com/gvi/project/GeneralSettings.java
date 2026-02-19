package com.gvi.project;

import java.awt.*;

public class GeneralSettings {
	private Dimension screenDimensions;
	public final int defaultTileSize = 16;
	public final int defaultTileScale = 3;

	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;

	public final int scaledTileSize = defaultTileSize * defaultTileScale;
	public int tileColumns = (int) Math.ceil(screenDimensions.getWidth() / scaledTileSize);
	public int tileRows = (int) Math.ceil(screenDimensions.getHeight() / scaledTileSize);

	public GeneralSettings () {
		this.screenDimensions = new Dimension(1280, 720);
	}

	public int getTileColumns() {
		return tileColumns;
	}

	public int getTileRows() {
		return tileRows;
	}

	public Dimension getScreenDimensions() {
		return screenDimensions;
	}

	public void setScreenDimensions(Dimension sd) {
		screenDimensions = sd;
	}

	public int getDefaultTileSize() {
		return defaultTileSize;
	}

	public int getDefaultTileScale() {
		return defaultTileScale;
	}

	public int getScaledTileSize() {
		return scaledTileSize;
	}

}
