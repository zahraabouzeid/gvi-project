package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.helper.ImageHelper;
import com.gvi.project.helper.TimeoutHelper;
import com.gvi.project.models.entities.Player;

import java.io.IOException;

public class OBJ_Boots extends SuperObject {
	public OBJ_Boots() {
		name = "Boots";
		interactHint = "[F] Equip Boots";
		canInteract = true;

		try {
			sprite.image = ImageHelper.getImage("/sprites/objects/boots.png");
			sprite.imageHeight = 1;
			sprite.imageWidth = 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		gp.playSE(2);
		player.speed = 8;
		gp.obj.remove(objIndex);
		gp.ui.openMessage("SPEED UP!");
		TimeoutHelper.setTimeout(() -> {
			player.speed = 4;
		}, 10000);
	}
}
