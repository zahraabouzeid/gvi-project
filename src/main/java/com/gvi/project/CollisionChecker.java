package com.gvi.project;

import com.gvi.project.models.objects.SuperObject;

public class CollisionChecker {
	GamePanel gp;
	public boolean[][] collisionMap;

	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}

	// Prüft, ob ein Tile im Grid begehbar ist (Wand, Wasser, Baum blockiert)
	// Gibt auch true zurück, wenn die Position außerhalb der Weltkarte liegt
	public boolean isTileBlocked(int gridX, int gridY) {
		if (gridX < 0 || gridX >= gp.currentMap.width || gridY < 0 || gridY >= gp.currentMap.height) {
			return true; // außerhalb der Karte wird blockiert
		}

		// Prüft über alle Layer der game Map
		for (var layer : gp.currentMap.gameMapLayers.values()){
			if (gp.spriteManager.getStoredSprite(layer.layout[gridX][gridY]).hasCollision){
				return true;
			}
		}

		return false;
	}

	// Prüft, ob ein blockierendes Objekt (z.B. Tür) auf dem Ziel-Tile steht
	// Vergleicht die Pixel-Position des Objekts mit dem Ziel-Grid-Feld
	public boolean isObjectBlocking(int gridX, int gridY) {
		int targetWorldX = gridX * gp.generalSettings.tileSize;
		int targetWorldY = gridY * gp.generalSettings.tileSize;
		for (SuperObject obj : gp.obj) {
			if (obj != null && obj.collision && obj.worldX == targetWorldX && obj.worldY == targetWorldY) {
				return true;
			}
		}
		return false;
	}
}
