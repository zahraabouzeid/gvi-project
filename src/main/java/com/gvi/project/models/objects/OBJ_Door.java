package com.gvi.project.models.objects;

import com.gvi.project.Components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.Sound;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.sprite_sheets.Sprite;

import java.util.ArrayList;
import java.util.List;

public class OBJ_Door extends AnimatedObject {
	ArrayList<Sprite> sprites;
	Sound sound = new Sound();

	public OBJ_Door() {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDoorUp", "door");
		name = "Door";
		interactHint = "[F] Unlock Door";
		spriteDirectionUp = true;
		collision = true;
		collisionBox.setWidth(2 * 16 * 3);
		canInteract = true;

		setUpAnimationComponent();
	}


	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		if (!canInteract) return;
		if (player.playerKeys == 0) {
//			player.playerKeys = 0;
			((AnimationComponent)components.get("Animation")).trigger();
			sound.setFile(4);
			sound.loop();
			sound.play();
			canInteract = false;
		}

	}

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.cycleLength = 1.5;
		animComp.onFinished = () -> {
			this.collision = false;
			this.sound.stop();
		};

		sprite = animComp.getCurrentSprite();
	};
}
