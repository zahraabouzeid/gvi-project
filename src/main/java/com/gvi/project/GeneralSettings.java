package com.gvi.project;


public class GeneralSettings {
	final int originalTileSize = 16;  // 16x16 px ein Tile ist 16x16 Pixel groß
	final int scale = 3;// 3x16 = 48x48 px pro Tile
	public final int tileSize = originalTileSize * scale; // 48x48 px
	public final int maxScreenCol = 35;
	public final int maxScreenRow = 22;
	public final int screenWidth = maxScreenCol *  tileSize;
	public final int screenHeight = maxScreenRow * tileSize;


	public GeneralSettings () {}
}
