package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.helper.ImageHelper;
import com.gvi.project.models.entities.Player;

import java.io.IOException;

public class OBJ_Door extends SuperObject {
	public OBJ_Door() {
		name = "Door";
		interactHint = "[F] Unlock Door";

		try {
			image = ImageHelper.getImage("/sprites/objects/door.png");
		} catch (IOException e) {
			e.printStackTrace();
		}

		collision = true;
	}

	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		if (player.playerKeys > 0) {
			gp.obj[objIndex] = null;
			gp.playSE(3);
			player.playerKeys--;
			gp.ui.openMessage("You opened the door!");
		} else {
			gp.ui.openMessage("You need a key!");
		}
	}
}
