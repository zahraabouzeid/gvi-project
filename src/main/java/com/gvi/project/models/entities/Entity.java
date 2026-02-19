package com.gvi.project.models.entities;

import javafx.scene.image.Image;

import java.awt.*;

public abstract class Entity {
	public int worldX, worldY;
	public int speed;

	// Grid-based Movement: Position im Tile-Raster (z.B. gridX=23 → worldX=1104px)
	public int gridX, gridY;

	// Das Tile, zu dem der Spieler gerade läuft
	public int targetGridX, targetGridY;

	// true = Spieler ist gerade zwischen zwei Tiles unterwegs
	public boolean isMoving = false;

	public Image up1, down1, left1, right1, up2, down2, left2, right2;
	public String direction;

	public int spriteCounter = 0;
	public int spriteNumber = 1;

	public Rectangle collisionBox;
	public int collisionBoxDefaultX, collisionBoxDefaultY;
	public boolean collisionActive = false;
}

