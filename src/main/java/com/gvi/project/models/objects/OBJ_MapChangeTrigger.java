package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.GeneralSettings;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.game_maps.GameMaps;
import com.gvi.project.models.sprite_sheets.SpriteSheet;

public class OBJ_MapChangeTrigger extends SuperObject {
	public int targetMapId;
	public int targetMapSpawnLocationX;
	public int targetMapSpawnLocationY;
	public String playerDirection;

	public OBJ_MapChangeTrigger(String playerDirection, String direction, int targetMapId, int targetMapSpawnLocationX, int targetMapSpawnLocationY) {
		this.targetMapId = targetMapId;
		this.targetMapSpawnLocationX = targetMapSpawnLocationX;
		this.targetMapSpawnLocationY = targetMapSpawnLocationY;
		this.playerDirection = playerDirection;
		this.visibleInMinimap = false;
		initImageLoad(direction);
		setCollisionBox(direction);
	}

	private void initImageLoad(String direction){
		SpriteSheet sheet = new SpriteSheet("/sprites/tilemaps/damp-dungeons/Tiles/light_samples");
		switch (direction){
			case "up":
				this.sprite = sheet.getSprite("yellow","top_to_bottom");
				this.sprite.imageHeight = 2;
			break;
			case "down":
				this.spriteDirectionUp = true;
				this.sprite = sheet.getSprite("yellow","bottom_to_top");
				this.sprite.imageOffsetY = -1;

			break;
			case "left":
				this.sprite = sheet.getSprite("yellow","left_to_right");
			break;
			case "right":
				this.sprite = sheet.getSprite("yellow","right_to_left");
			break;
		}
	}

    private void setCollisionBox(String direction){
		switch (direction){
			case "up":
			case "down":
				this.collisionBox.setHeight(GeneralSettings.getTileSize());
				this.collisionBox.setWidth(GeneralSettings.getTileSize() * 2);
				break;
			case "left":
			case "right":
				if (spriteDirectionUp){
					collisionBox.setY(-GeneralSettings.getTileSize() * (sprite.imageHeight - 1));
				}
				this.collisionBox.setHeight(GeneralSettings.getTileSize() * 2);
				this.collisionBox.setWidth(GeneralSettings.getTileSize());
				break;
		}
	}

    @Override
	public void onStep(Player player, GamePanel gp, int objIndex) {
		int xDiff = player.gridX - this.worldX / GeneralSettings.getTileSize();
		int yDiff = player.gridY - this.worldY / GeneralSettings.getTileSize();

		gp.playSE(3);
		gp.loadMap(GameMaps.fromId(targetMapId));
		player.setPlayerPosition(targetMapSpawnLocationX + xDiff, targetMapSpawnLocationY + yDiff);
		player.direction = playerDirection;
	}
}
