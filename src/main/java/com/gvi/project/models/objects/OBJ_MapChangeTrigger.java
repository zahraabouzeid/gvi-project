package com.gvi.project.models.objects;

import com.gvi.project.models.sprite_sheets.SpriteSheet;

public class OBJ_MapChangeTrigger extends SuperObject {
	public int targetMapId;
	public int targetMapSpawnLocationX;
	public int targetMapSpawnLocationY;

	public OBJ_MapChangeTrigger(String direction) {
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
}
