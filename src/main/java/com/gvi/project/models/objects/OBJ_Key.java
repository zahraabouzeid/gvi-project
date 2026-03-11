package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.helper.ImageHelper;
import com.gvi.project.models.entities.Player;

import java.io.IOException;

public class OBJ_Key extends SuperObject {
	public OBJ_Key() {
		name = "Key";
		interactHint = "[F] Pick up Key";

		try {
			sprite.image = ImageHelper.getImage("/sprites/objects/key.png");
			sprite.imageHeight = 1;
			sprite.imageWidth = 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		gp.playSE(1);
		player.playerKeys++;
		gp.obj.remove(objIndex);
		gp.ui.openMessage("You got a key!");
	}
}
