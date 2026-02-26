package com.gvi.project.models.entities;

import com.gvi.project.GamePanel;
import com.gvi.project.KeyHandler;
import com.gvi.project.helper.ImageHelper;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.IOException;


public class Player extends Entity {
	private GamePanel gp;
	private KeyHandler keyH;

	public final int screenX;
	public final int screenY;

	public String playerName = "Player";
	public int playerKeys = 0;
	public int nearbyObjectIndex = -1;
	public int maxHealthHalf = 10;
	public int healthHalf = 10;
	public boolean isDead = false;
	public int score = 0;


	public Player(GamePanel gp, KeyHandler keyH) {
		this.gp = gp;
		this.keyH = keyH;

		screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
		screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

		this.collisionBox = new Rectangle(8, 16, 32, 32);

		collisionBoxDefaultX = collisionBox.x;
		collisionBoxDefaultY = collisionBox.y;

		setDefaultValues();
		getPlayerSprites();
	}

	public void setDefaultValues() {
		// Startposition des Spielers im Grid (Tile-Koordinaten, nicht Pixel)
		gridX = 23; // vorher worldX = 48 * 23 = 1104px jetzt ist es grid movement
		gridY = 23;

		// Ziel-Tile ist am Anfang gleich wie die aktuelle Position (kein laufende Bewegung)
		targetGridX = gridX;
		targetGridY = gridY;

		// Pixel-Position berechnen: gridX * 48px = worldX
		worldX = gp.tileSize * gridX;
		worldY = gp.tileSize * gridY;

		isMoving = false;

		// 4 Pixel pro Frame → 48px / 4 = 12 Frames pro Tile-Übergang
		speed = 4;
		direction = "down";
		maxHealthHalf = 10;
		healthHalf = 10;
		isDead = false;
		score = 0;
	}

	public void takeHalfHeartDamage() {
		if (isDead) return;
		healthHalf = Math.max(0, healthHalf - 1);
		if (healthHalf == 0) {
			isDead = true;
		}
	}

	public void getPlayerSprites() {
		try {
			up1 = ImageHelper.getImage("/sprites/entities/player/player_up_1.png");
			up2 = ImageHelper.getImage("/sprites/entities/player/player_up_2.png");
			down1 = ImageHelper.getImage("/sprites/entities/player/player_down_1.png");
			down2 = ImageHelper.getImage("/sprites/entities/player/player_down_2.png");
			left1 = ImageHelper.getImage("/sprites/entities/player/player_left_1.png");
			left2 = ImageHelper.getImage("/sprites/entities/player/player_left_2.png");
			right1 = ImageHelper.getImage("/sprites/entities/player/player_right_1.png");
			right2 = ImageHelper.getImage("/sprites/entities/player/player_right_2.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// bewegung und animation
	public void update() {
		if (isMoving) {
			// Ziel-Position in Pixel berechnen (targetGridX/Y * 48px)
			int targetWorldX = targetGridX * gp.tileSize;
			int targetWorldY = targetGridY * gp.tileSize;

			// Spieler Schritt für Schritt Richtung Ziel bewegen (4px pro Frame)
			// Math.min/max verhindert, dass der Spieler über das Ziel hinausschießt
			if (worldX < targetWorldX) worldX = Math.min(worldX + speed, targetWorldX);
			else if (worldX > targetWorldX) worldX = Math.max(worldX - speed, targetWorldX);

			if (worldY < targetWorldY) worldY = Math.min(worldY + speed, targetWorldY);
			else if (worldY > targetWorldY) worldY = Math.max(worldY - speed, targetWorldY);

			// Sprite-Animation: alle 12 Frames zwischen Bild 1 und 2 wechseln
			spriteCounter++;
			if (spriteCounter > 12) {
				spriteNumber = (spriteNumber == 1) ? 2 : 1;
				spriteCounter = 0;
			}

			// Prüfen ob der Spieler das Ziel-Tile exakt erreicht hat
			if (worldX == targetWorldX && worldY == targetWorldY) {
				// Grid-Position aktualisieren und Bewegung stoppen
				gridX = targetGridX;
				gridY = targetGridY;
				isMoving = false;
			}
		} else {
			// Eingaber verarbeiten nur wenn der Spieler stillsteht
			if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
				// Nächste Grid-Position berechnen basierend auf gedrückter Taste
				int nextGridX = gridX;
				int nextGridY = gridY;

				if (keyH.upPressed)         { direction = "up";    nextGridY--; }
				else if (keyH.downPressed)  { direction = "down";  nextGridY++; }
				else if (keyH.leftPressed)  { direction = "left";  nextGridX--; }
				else if (keyH.rightPressed) { direction = "right"; nextGridX++; }

				// Kollision prüfen: Tile und Objekte am Ziel-Grid-Feld
				// Nur wenn frei Bewegung starten
				if (!gp.cChecker.isTileBlocked(nextGridX, nextGridY) &&
						!gp.cChecker.isObjectBlocking(nextGridX, nextGridY)) {
					targetGridX = nextGridX;
					targetGridY = nextGridY;
					isMoving = true;
				}
			}
		}

		// Jedes Frame prüfen ob ein Objekt in Reichweite ist (1 Tile Abstand)
		nearbyObjectIndex = findNearbyObject();

		// F-Taste: Objekt in der Nähe interagieren (z.B. Quiz-Station öffnen)
		if (keyH.fPressed) {
			keyH.fPressed = false;
			if (nearbyObjectIndex != -1 && gp.obj[nearbyObjectIndex] != null) {
				gp.obj[nearbyObjectIndex].onInteract(this, gp, nearbyObjectIndex);
			}
		}
	}

	private int findNearbyObject() {
		int interactRange = gp.tileSize;

		int playerCenterX = worldX + collisionBox.x + collisionBox.width / 2;
		int playerCenterY = worldY + collisionBox.y + collisionBox.height / 2;

		for (int i = 0; i < gp.obj.length; i++) {
			if (gp.obj[i] != null) {
				int objCenterX = gp.obj[i].worldX + gp.tileSize / 2;
				int objCenterY = gp.obj[i].worldY + gp.tileSize / 2;

				int dx = Math.abs(playerCenterX - objCenterX);
				int dy = Math.abs(playerCenterY - objCenterY);

				if (dx <= interactRange && dy <= interactRange) {
					return i;
				}
			}
		}
		return -1;
	}

	public void draw(GraphicsContext gc) {
		Image image = null;

		switch (direction) {
			case "up":

				if (spriteNumber == 1) {
					image = up1;
				}
				if (spriteNumber == 2) {
					image = up2;
				}
				break;
			case "down":
				if (spriteNumber == 1) {
					image = down1;
				}
				if (spriteNumber == 2) {
					image = down2;
				}
				break;
			case "left":
				if (spriteNumber == 1) {
					image = left1;
				}
				if (spriteNumber == 2) {
					image = left2;
				}
				break;
			case "right":
				if (spriteNumber == 1) {
					image = right1;
				}
				if (spriteNumber == 2) {
					image = right2;
				}
				break;
		}

		gc.drawImage(image, this.screenX, this.screenY, gp.tileSize, gp.tileSize);
	}
}
