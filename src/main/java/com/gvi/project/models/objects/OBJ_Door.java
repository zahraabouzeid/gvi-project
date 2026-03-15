package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.sprite_sheets.Sprite;
import com.gvi.project.models.sprite_sheets.SpriteSheet;

import java.util.Map;

public class OBJ_Door extends SuperObject {
	Map<String, Sprite> sprites;

	public OBJ_Door() {
		name = "Door";
		interactHint = "[F] Unlock Door";

		spriteDirectionUp = true;

		SpriteSheet spriteSheet = new SpriteSheet("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDoorUp");
		sprites = spriteSheet.getGroupSprites("door");

		sprite = sprites.get("frame_0");

		collision = true;
		collisionBox.setWidth(2 * 16 * 3);
	}

	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		if (player.playerKeys > 0) {
			gp.obj.remove(objIndex);
			gp.playSE(3);
			player.playerKeys--;
			gp.ui.openMessage("You opened the door!");
		} else {
			gp.ui.openMessage("You need a key!");
		}
	}
}
