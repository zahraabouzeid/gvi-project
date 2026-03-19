package com.gvi.project.models.core;

import com.gvi.project.models.sprite_sheets.Sprite;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

public abstract class Entity implements Renderable{
	public int worldX, worldY;
	public int speed;

	// Grid-based Movement: Position im Tile-Raster (z.B. gridX=23 → worldX=1104px)
	public int gridX, gridY;

	// Das Tile, zu dem der Spieler gerade läuft
	public int targetGridX, targetGridY;

	// true = Spieler ist gerade zwischen zwei Tiles unterwegs
	public boolean isMoving = false;

	public Map<String,Sprite> spriteMap;
	public String direction;

	public int spriteCounter = 0;
	public int spriteNumber = 1;

	public Rectangle collisionBox;
	public int collisionBoxDefaultX, collisionBoxDefaultY;
	public boolean collisionActive = false;

	public Entity (){
		spriteMap = new HashMap<String,Sprite>();
	}

	@Override
	public int getY() {
		return worldY;
	}
}

