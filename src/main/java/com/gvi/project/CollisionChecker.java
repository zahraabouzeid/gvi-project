package com.gvi.project;

import com.gvi.project.models.objects.SuperObject;

public class CollisionChecker {
	GamePanel gp;

	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}

	// Prüft, ob ein Tile im Grid begehbar ist (Wand, Wasser, Baum blockiert)
	// Gibt auch true zurück, wenn die Position außerhalb der Weltkarte liegt
	public boolean isTileBlocked(int gridX, int gridY) {
		if (gridX < 0 || gridX >= gp.maxWorldCol || gridY < 0 || gridY >= gp.maxWorldRow) {
			return true; // außerhalb der Karte wird blockiert
		}
		int tileNum = gp.tileManager.mapTileNumber[gridX][gridY];
		return gp.tileManager.tiles[tileNum].hasCollision;
	}

	// Prüft, ob ein blockierendes Objekt (z.B. Tür) auf dem Ziel-Tile steht
	// Vergleicht die Pixel-Position des Objekts mit dem Ziel-Grid-Feld
	public boolean isObjectBlocking(int gridX, int gridY) {
		int targetWorldX = gridX * gp.tileSize;
		int targetWorldY = gridY * gp.tileSize;
		for (SuperObject obj : gp.obj) {
			if (obj != null && obj.collision && obj.worldX == targetWorldX && obj.worldY == targetWorldY) {
				return true;
			}
		}
		return false;
	}
}
