package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.game_maps.GameMaps;
import com.gvi.project.models.sprite_sheets.SpriteSheet;

public class OBJ_MapChangeTrigger extends SuperObject {
	public int targetMapId;
	public int targetMapSpawnLocationX;
	public int targetMapSpawnLocationY;

	public OBJ_MapChangeTrigger(String direction) {
		initImageLoad(direction);
		setCollisionBox(direction);
	}

	public OBJ_MapChangeTrigger(String direction, int targetMapId, int targetMapSpawnLocationX, int targetMapSpawnLocationY) {
		this(direction);
		this.targetMapId = targetMapId;
		this.targetMapSpawnLocationX = targetMapSpawnLocationX;
		this.targetMapSpawnLocationY = targetMapSpawnLocationY;
	}

	private void initImageLoad(String direction){
		SpriteSheet sheet = new SpriteSheet("/sprites/tilemaps/damp-dungeons/Tiles/light_samples");
		switch (direction){
			case "up":
				this.sprite = sheet.getSprite("yellow","top_to_bottom");
				this.sprite.imageHeight = 2;
			break;
			case "down":
				this.sprite = sheet.getSprite("yellow","bottom_to_top");
			break;
			case "left":
				this.sprite = sheet.getSprite("yellow","left_to_right");
			break;
			case "right":
				this.sprite = sheet.getSprite("yellow","right_to_left");
			break;
		}
	};

	private void setCollisionBox(String direction){
		switch (direction){
			case "up":
			case "down":
				this.collisionBox.height = 16 * 3;
				this.collisionBox.width = 16 * 3 * 2;
				break;
			case "left":
			case "right":
				this.collisionBox.height = 16 * 3 * 2;
				this.collisionBox.width = 16 * 3;
				break;
		}
	};

	@Override
	public void onStep(Player player, GamePanel gp, int objIndex) {
		gp.loadMap(GameMaps.fromId(targetMapId));
		player.gridX = targetMapSpawnLocationX;
		player.gridY = targetMapSpawnLocationY;
		player.targetGridX = player.gridX;
		player.targetGridY = player.gridY;
		player.worldX = player.gridX * gp.generalSettings.tileSize;
		player.worldY = player.gridY * gp.generalSettings.tileSize;
	}
}
