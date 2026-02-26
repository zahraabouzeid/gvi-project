package com.gvi.project.models.tiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gvi.project.GamePanel;
import com.gvi.project.models.game_levels.GameLevelLayer;
import com.gvi.project.models.game_levels.config.GameLevelConfig;
import com.gvi.project.models.game_levels.GameLevels;
import com.gvi.project.models.game_levels.config.GameLevelLayerConfig;
import com.gvi.project.models.game_levels.config.GameLevelSpriteSheetConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TileManager {
	private ObjectMapper mapper = new ObjectMapper();
	private GamePanel gp;

	private Map<String, int[][]> currentGameLevelLayers;

	public TileManager(GamePanel gp) {
		this.gp = gp;
		loadMap(GameLevels.TEST_LEVEL);
	}

	public void loadGameLevelLayers(GameLevelConfig levelConfig) {
		for (GameLevelLayerConfig layerConfig : levelConfig.layers){
			GameLevelLayer gameLevelLayer = new GameLevelLayer(layerConfig, levelConfig.width, levelConfig.height);
			currentGameLevelLayers.put(layerConfig.id, layerConfig.spriteLayout);

			loadUsedSprites(layerConfig);
		}
	}

	public void loadUsedSprites(GameLevelLayerConfig layerConfig){
		for (GameLevelSpriteSheetConfig spriteSheetConfig : layerConfig.usedSpriteSheets){

		}
	}

	public void loadMap(GameLevels gameLevel) {
		GameLevelConfig levelConfig = null;

		try (InputStream is = getClass().getResourceAsStream(
				"/maps/" + gameLevel.getConfigFileName())) {

			if (is == null) {
				throw new RuntimeException("File not found in resources");
			}

			levelConfig = mapper.readValue(is, GameLevelConfig.class);

		} catch (IOException e) {
			System.out.println("Error while parsing file: " + gameLevel.getConfigFileName());
			e.printStackTrace();
		}

		if (levelConfig != null) {
			loadGameLevelLayers(levelConfig);
		}
	}

	public void draw(GraphicsContext gc) {

		int worldCol = 0;
		int worldRow = 0;

		while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

			//int tileNum = mapTileNumber[worldCol][worldRow];

			int worldX = worldCol * gp.tileSize;
			int worldY = worldRow * gp.tileSize;
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;

			if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
					worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
					worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
					worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
				// gc.drawImage(tiles[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize);
			}

			worldCol++;

			if (worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
		}
	}
}
