package com.gvi.project.models.objects;

import com.gvi.project.Components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.helper.ImageHelper;
import com.gvi.project.models.entities.Player;

import java.io.IOException;

public class OBJ_Key extends AnimatedObject {
	public OBJ_Key(String spriteGroupID) {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDungeon", spriteGroupID);
		name = "Key";
		canInteract = true;
		interactHint = "[F] Pick up Key";
		collision = true;
		setUpAnimationComponent();
	}

	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		gp.playSE(1);
		player.playerKeys++;
		gp.obj.remove(objIndex);
		gp.ui.openMessage("You got a key!");
	}

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.isLooping();
		animComp.cycleLength = 0.5;
		animComp.delayBetweenCycles = 0.8;

		sprite = animComp.getCurrentSprite();
	};
}
