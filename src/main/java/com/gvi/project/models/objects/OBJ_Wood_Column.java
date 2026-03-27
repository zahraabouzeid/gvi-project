package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.models.sprite_sheets.SpriteSheet;


public class OBJ_Wood_Column extends SuperObject {
	public OBJ_Wood_Column() {
		setDefaultValues();
	}

	public void setDefaultValues(){
		SpriteSheet sheet = new SpriteSheet("/sprites/tilemaps/damp-dungeons/Tiles/DungeonDecorations");
		sprite = sheet.getSprite("wood_colum_beam_framing","column_base");
		sprite.imageHeight = 2;
		sprite.imageWidth = 1;
		spriteDirectionUp = true;
		collision = true;
		visibleInMinimap = false;
	}

	@Override
	public void onConfirm(GamePanel gp, int objIndex) {
		// Berechne Belohnung und zeige Winscreen nur bei neuer Medaille
		gp.ui.calculateReward();
		if (gp.ui.shouldShowWinScreen()) {
			gp.ui.gameFinished = true;
			gp.stopMusic();
			gp.playSE(4);
		}
	}

}
