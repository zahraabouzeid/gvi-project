package com.gvi.project;


public class GeneralSettings {
	private final static int originalTileSize = 16;  // 16x16 px ein Tile ist 16x16 Pixel groß
	private final static int scale = 3;// 3x16 = 48x48 px pro Tile
	private static final int tileSize = originalTileSize * scale; // 48x48 px
	private static final int maxScreenCol = 35;
	private static final int maxScreenRow = 22;
	private static final int screenWidth = maxScreenCol *  tileSize;
	private static final int screenHeight = maxScreenRow * tileSize;
	private static boolean isDevMode = true;
	private static int FPS = 60;
	private static double drawInterval = 1000000000.0 / FPS;

	public GeneralSettings () {}

	public static int getScreenWidth() {
		return screenWidth;
	}
	public static int getScreenHeight() {
		return screenHeight;
	}
	public static int getOriginalTileSize() {
		return originalTileSize;
	}
	public static int getScale() {
		return scale;
	}
	public static int getTileSize() {
		return tileSize;
	}
	public static int getMaxScreenCol() {
		return maxScreenCol;
	}
	public static int getMaxScreenRow() {
		return maxScreenRow;
	}
	public static double getDrawInterval() {
		return drawInterval;
	}
	public static boolean isDevMode() {
		return isDevMode;
	}
	public static void setDevMode(boolean isDevMode) {
		GeneralSettings.isDevMode = isDevMode;
	}
	public static void setFPS(int fps) {
		FPS = fps;
	}

}
