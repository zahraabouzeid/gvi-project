package com.gvi.project;

import com.gvi.project.models.objects.SuperObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollisionChecker {
	private static final Logger log = LoggerFactory.getLogger(CollisionChecker.class);
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
		return collisionMap[gridX][gridY];
	}

	// Prüft, ob ein blockierendes Objekt (z.B. Tür) auf dem Ziel-Tile steht
	// Vergleicht die Pixel-Position des Objekts mit dem Ziel-Grid-Feld
	public boolean isObjectBlocking(int gridX, int gridY) {
		int targetX = gridX * GeneralSettings.getTileSize() + (int) gp.player.collisionBox.getX();
		int targetY = gridY * GeneralSettings.getTileSize() + (int) gp.player.collisionBox.getY();
		int targetW = (int) gp.player.collisionBox.getWidth();
		int targetH = (int) gp.player.collisionBox.getHeight();

		for (SuperObject obj : gp.obj) {
			if (obj != null && obj.collision) {
				int objX = obj.worldX + (int) obj.collisionBox.getX();
				int objY = obj.worldY + (int) obj.collisionBox.getY();
				int objW = (int) obj.collisionBox.getWidth();
				int objH = (int) obj.collisionBox.getHeight();

				if (targetX < objX + objW &&
					targetX + targetW > objX &&
					targetY < objY + objH &&
					targetY + targetH > objY
				) {
					return true;
				}
			}
		}

		return false;
	}

	public void printCollisionMap(){
		StringBuilder output = new StringBuilder();

		for(int y = 0; y< collisionMap.length; y++){
			for(int x = 0; x< collisionMap[y].length; x++){
				output.append("|").append(collisionMap[y][x] ? "1" : " ");
			}
			output.append("|\n");
		}

		log.debug("Collision map:\n{}", output);
	}
}
