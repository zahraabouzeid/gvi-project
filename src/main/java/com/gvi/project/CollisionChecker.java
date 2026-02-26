package com.gvi.project;

import com.gvi.project.models.core.Entity;

public class CollisionChecker {
	GamePanel gp;

	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}

	public void checkCollision(Entity entity) {
		int entityLeftWorldX = entity.worldX + entity.collisionBox.x;
		int entityRightWorldX = entity.worldX + entity.collisionBox.x + entity.collisionBox.width;
		int entityTopWorldY = entity.worldY + entity.collisionBox.y;
		int entityBottomWorldY = entity.worldY + entity.collisionBox.y + entity.collisionBox.height;

		int entityLeftCol = entityLeftWorldX / gp.tileSize;
		int entityRightCol = entityRightWorldX / gp.tileSize;
		int entityTopRow = entityTopWorldY / gp.tileSize;
		int entityBottomRow = entityBottomWorldY / gp.tileSize;

		int tileNumber1, tileNumber2;
		/*
		switch (entity.direction) {
			case "up":
				entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
				tileNumber1 = gp.tileManager.mapTileNumber[entityLeftCol][entityTopRow];
				tileNumber2 = gp.tileManager.mapTileNumber[entityRightCol][entityTopRow];

				if (gp.tileManager.tiles[tileNumber1].hasCollision || gp.tileManager.tiles[tileNumber2].hasCollision) {
					entity.collisionActive = true;
				}
				break;
			case "down":
				entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
				tileNumber1 = gp.tileManager.mapTileNumber[entityLeftCol][entityBottomRow];
				tileNumber2 = gp.tileManager.mapTileNumber[entityRightCol][entityBottomRow];

				if (gp.tileManager.tiles[tileNumber1].hasCollision || gp.tileManager.tiles[tileNumber2].hasCollision) {
					entity.collisionActive = true;
				}
				break;
			case "left":
				entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
				tileNumber1 = gp.tileManager.mapTileNumber[entityLeftCol][entityTopRow];
				tileNumber2 = gp.tileManager.mapTileNumber[entityLeftCol][entityBottomRow];

				if (gp.tileManager.tiles[tileNumber1].hasCollision || gp.tileManager.tiles[tileNumber2].hasCollision) {
					entity.collisionActive = true;
				}
				break;
			case "right":
				entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
				tileNumber1 = gp.tileManager.mapTileNumber[entityRightCol][entityTopRow];
				tileNumber2 = gp.tileManager.mapTileNumber[entityRightCol][entityBottomRow];

				if (gp.tileManager.tiles[tileNumber1].hasCollision || gp.tileManager.tiles[tileNumber2].hasCollision) {
					entity.collisionActive = true;
				}
				break;
		}

		 */
	}

	public int checkObject(Entity entity, boolean isPlayer){
		int index = 999;

		for (int i = 0; i < gp.obj.length; i++) {
			if(gp.obj[i] != null){
				entity.collisionBox.x = entity.collisionBox.x + entity.worldX;
				entity.collisionBox.y = entity.collisionBox.y + entity.worldY;

				gp.obj[i].collisionBox.x = gp.obj[i].collisionBox.x + gp.obj[i].worldX;
				gp.obj[i].collisionBox.y = gp.obj[i].collisionBox.y + gp.obj[i].worldY;


				switch (entity.direction) {
					case "up":
						entity.collisionBox.y -= entity.speed;
						if(entity.collisionBox.intersects(gp.obj[i].collisionBox)){
							if(gp.obj[i].collision){
								entity.collisionActive = true;
							}
							if(isPlayer){
								index = i;
							}
						}
						break;
					case "down":
						entity.collisionBox.y += entity.speed;
						if(entity.collisionBox.intersects(gp.obj[i].collisionBox)){
							if(gp.obj[i].collision){
								entity.collisionActive = true;
							}
							if(isPlayer){
								index = i;
							}
						}
						break;
					case "left":
						entity.collisionBox.x -= entity.speed;
						if(entity.collisionBox.intersects(gp.obj[i].collisionBox)){
							if(gp.obj[i].collision){
								entity.collisionActive = true;
							}
							if(isPlayer){
								index = i;
							}
						}
						break;
					case "right":
						entity.collisionBox.x += entity.speed;
						if(entity.collisionBox.intersects(gp.obj[i].collisionBox)){
							if(gp.obj[i].collision){
								entity.collisionActive = true;
							}
							if(isPlayer){
								index = i;
							}
						}
						break;
				}
				entity.collisionBox.x = entity.collisionBoxDefaultX;
				entity.collisionBox.y = entity.collisionBoxDefaultY;
				gp.obj[i].collisionBox.x = gp.obj[i].collisionBoxDefaultX;
				gp.obj[i].collisionBox.y = gp.obj[i].collisionBoxDefaultY;
			}
		}

		return index;
	}
}
