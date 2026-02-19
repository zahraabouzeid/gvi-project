package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.helper.ImageHelper;
import com.gvi.project.models.entities.Player;

import java.io.IOException;

public class OBJ_Chest extends SuperObject {
	public OBJ_Chest() {
		name = "Chest";
		interactHint = "[F] Open Chest";

		try {
			image = ImageHelper.getImage("/sprites/objects/chest.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		gp.ui.gameFinished = true;
		gp.stopMusic();
		gp.playSE(4);
	}
}
