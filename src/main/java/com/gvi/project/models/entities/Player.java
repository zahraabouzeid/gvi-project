package com.gvi.project.models.entities;

import com.gvi.project.GamePanel;
import com.gvi.project.KeyHandler;
import com.gvi.project.models.core.Entity;
import com.gvi.project.models.objects.SuperObject;
import com.gvi.project.models.sprite_sheets.Sprite;
import com.gvi.project.models.sprite_sheets.SpriteSheet;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

		screenX = gp.generalSettings.screenWidth / 2 - (gp.generalSettings.tileSize / 2);
		screenY = gp.generalSettings.screenHeight / 2 - (gp.generalSettings.tileSize / 2);

		this.collisionBox = new Rectangle(0,0, 48, 48);

		collisionBoxDefaultX = (int) collisionBox.getX();
		collisionBoxDefaultY = (int) collisionBox.getY();

		setDefaultValues();
		getPlayerSprites();

		this.gp.entityList.add(this);
	}

	public void setDefaultValues() {
		// Startposition des Spielers im Grid (Tile-Koordinaten, nicht Pixel)
		gridX = 4; // vorher worldX = 48 * 23 = 1154px jetzt ist es grid movement
		gridY = 6;

		// Ziel-Tile ist am Anfang gleich wie die aktuelle Position (kein laufende Bewegung)
		targetGridX = gridX;
		targetGridY = gridY;

		// Pixel-Position berechnen: gridX * 48px = worldX
		worldX = gp.generalSettings.tileSize * gridX;
		worldY = gp.generalSettings.tileSize * gridY;

		isMoving = false;

		// 4 Pixel pro Frame → 48px / 4 = 12 Frames pro Tile-Übergang
		speed = 4;
		direction = "down";
		maxHealthHalf = 10;
		healthHalf = 10;
		isDead = false;
		score = 0;
	}

	public void setPlayerPosition(int gridPosX, int gridPosY){
		this.gridX = gridPosX;
		this.gridY = gridPosY;
		this.targetGridX = this.gridX;
		this.targetGridY = this.gridY;
		this.worldX = this.gridX * gp.generalSettings.tileSize;
		this.worldY = this.gridY * gp.generalSettings.tileSize;
	}

	public void takeHalfHeartDamage() {
		if (isDead) return;
		healthHalf = Math.max(0, healthHalf - 1);
		if (healthHalf == 0) {
			isDead = true;
		}
	}

	public void getPlayerSprites() {
		SpriteSheet sheet = new SpriteSheet("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_HeroMan1");

		spriteMap.put("up_1", sheet.getSprite("walk", "up_1"));
		spriteMap.put("up_2", sheet.getSprite("walk", "up_2"));
		spriteMap.put("down_1", sheet.getSprite("walk", "down_1"));
		spriteMap.put("down_2", sheet.getSprite("walk", "down_2"));
		spriteMap.put("left_1", sheet.getSprite("walk", "left_1"));
		spriteMap.put("left_2", sheet.getSprite("walk", "left_2"));
		spriteMap.put("right_1", sheet.getSprite("walk", "right_1"));
		spriteMap.put("right_2", sheet.getSprite("walk", "right_2"));
	}

	// bewegung und animation
	public void update() {
		if (isMoving) {
			// Ziel-Position in Pixel berechnen (targetGridX/Y * 48px)
			int targetWorldX = targetGridX * gp.generalSettings.tileSize;
			int targetWorldY = targetGridY * gp.generalSettings.tileSize;

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
				checkStepObjects();
			}
		} else {
			// Eingabe verarbeiten nur wenn der Spieler stillsteht
			if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
				// Nächste Grid-Position berechnen basierend auf gedrückter Taste
				int nextGridX = gridX;
				int nextGridY = gridY;

				// Multi-Key-Input: Beide Achsen separat prüfen für diagonale Bewegung
				if (keyH.upPressed)    nextGridY--;
				if (keyH.downPressed)  nextGridY++;
				if (keyH.leftPressed)  nextGridX--;
				if (keyH.rightPressed) nextGridX++;

				// Direction für Sprite-Animation bestimmen
				// Priorität: horizontale Bewegung bei Diagonalen
				if (keyH.leftPressed) {
					direction = "left";
				} else if (keyH.rightPressed) {
					direction = "right";
				} else if (keyH.upPressed) {
					direction = "up";
				} else if (keyH.downPressed) {
					direction = "down";
				}

				// Kollision prüfen: Tile und Objekte am Ziel-Grid-Feld
				// Nur wenn frei Bewegung starten
				if (!gp.cChecker.isTileBlocked(nextGridX, nextGridY) && !gp.cChecker.isObjectBlocking(nextGridX, nextGridY)) {
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
			if (nearbyObjectIndex != -1 && gp.obj.get(nearbyObjectIndex) != null) {
				gp.obj.get(nearbyObjectIndex).onInteract(this, gp, nearbyObjectIndex);
			}
		}
	}

	private void checkStepObjects() {
		int targetX = worldX + (int) collisionBox.getX();
		int targetY = worldY + (int) collisionBox.getY();
		int targetW = (int) collisionBox.getWidth();
		int targetH = (int) collisionBox.getHeight();

		for (int i = 0; i < gp.obj.size(); i++) {
			SuperObject obj = gp.obj.get(i);

			if (obj != null && !obj.collision) {
				int objX = obj.worldX + (int) obj.collisionBox.getX();
				int objY = obj.worldY + (int) obj.collisionBox.getY();
				int objW = (int) obj.collisionBox.getWidth();
				int objH = (int) obj.collisionBox.getHeight();

				if (targetX < objX + objW &&
						targetX + targetW > objX &&
						targetY < objY + objH &&
						targetY + targetH > objY
				) {
					obj.onStep(this, gp, i);
					return;
				}
			}
		}
	}

	private int findNearbyObject() {
		int interactRange = gp.generalSettings.tileSize;

		int playerCenterX = (int)(worldX + collisionBox.getX() + collisionBox.getWidth() / 2);
		int playerCenterY = (int)(worldY + collisionBox.getY() + collisionBox.getHeight() / 2);

		for (int i = 0; i < gp.obj.size(); i++) {
			if (gp.obj.get(i) != null) {
				int objCenterX = gp.obj.get(i).worldX + gp.generalSettings.tileSize / 2;
				int objCenterY = gp.obj.get(i).worldY + gp.generalSettings.tileSize / 2;

				int dx = Math.abs(playerCenterX - objCenterX);
				int dy = Math.abs(playerCenterY - objCenterY);

				if (dx <= interactRange && dy <= interactRange && gp.obj.get(i).canInteract) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public void render(GamePanel gp) {
		Sprite sprite = null;
		int tileSize = gp.generalSettings.tileSize;

		switch (direction) {
			case "up":
				if (spriteNumber == 1) {
					sprite = spriteMap.get("up_1");
				}
				if (spriteNumber == 2) {
					sprite = spriteMap.get("up_2");
				}
				break;
			case "down":
				if (spriteNumber == 1) {
					sprite = spriteMap.get("down_1");
				}
				if (spriteNumber == 2) {
					sprite = spriteMap.get("down_2");
				}
				break;
			case "left":
				if (spriteNumber == 1) {
					sprite = spriteMap.get("left_1");
				}
				if (spriteNumber == 2) {
					sprite = spriteMap.get("left_2");
				}
				break;
			case "right":
				if (spriteNumber == 1) {
					sprite = spriteMap.get("right_1");
				}
				if (spriteNumber == 2) {
					sprite = spriteMap.get("right_2");
				}
				break;
		}

		assert sprite != null;

		gp.gc.drawImage(sprite.image, this.screenX, this.screenY - (sprite.imageHeight - 1) * tileSize , tileSize * sprite.imageWidth, tileSize * sprite.imageHeight);
	}


	@Override
	public void renderCollisionBox(GamePanel gp) {
		if(gp.generalSettings.isDevMode){
			gp.gc.setFill(new Color(0,0,1,0.3));
			gp.gc.fillRect(screenX + collisionBox.getX(), this.screenY + collisionBox.getY(), this.collisionBox.getWidth(), this.collisionBox.getHeight());
		}
	}
}
