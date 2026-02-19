package com.gvi.project.models.tiles;

import com.gvi.project.GamePanel;
import com.gvi.project.helper.ImageHelper;
import javafx.scene.canvas.GraphicsContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
	GamePanel gp;
	public Tile[] tiles;
	public int[][] mapTileNumber;

	public TileManager(GamePanel gp) {
		this.gp = gp;
		tiles = new Tile[10];
		mapTileNumber = new int[gp.maxWorldCol][gp.maxWorldRow];
		getTileImage();
		loadMap("/maps/map_01.txt");
	}

	public void getTileImage() {
		try{
			tiles[0] = new Tile();
			tiles[0].image = ImageHelper.getImage("/sprites/tiles/grass.png");

			tiles[1] = new Tile();
			tiles[1].image = ImageHelper.getImage("/sprites/tiles/wall.png");
			tiles[1].hasCollision = true;

			tiles[2] = new Tile();
			tiles[2].image = ImageHelper.getImage("/sprites/tiles/water.png");
			tiles[2].hasCollision = true;

			tiles[3] = new Tile();
			tiles[3].image = ImageHelper.getImage("/sprites/tiles/earth.png");

			tiles[4] = new Tile();
			tiles[4].image = ImageHelper.getImage("/sprites/tiles/tree.png");
			tiles[4].hasCollision = true;

			tiles[5] = new Tile();
			tiles[5].image = ImageHelper.getImage("/sprites/tiles/sand.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadMap(String filePath) {
		try {
			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			int col = 0;
			int row = 0;

			while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
				String line = br.readLine();

				while (col < gp.maxWorldCol) {
					String numbers[] = line.split(" ");

					int num = Integer.parseInt(numbers[col]);

					mapTileNumber[col][row] = num;
					col++;
				}

				if (col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void draw(GraphicsContext gc) {

		int worldCol = 0;
		int worldRow = 0;

		while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

			int tileNum = mapTileNumber[worldCol][worldRow];

			int worldX = worldCol * gp.tileSize;
			int worldY = worldRow * gp.tileSize;
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;

			if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
					worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
					worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
					worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
				gc.drawImage(tiles[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize);
			}

			worldCol++;

			if (worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
		}
	}
}
