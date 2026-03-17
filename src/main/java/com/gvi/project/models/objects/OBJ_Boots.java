package com.gvi.project.models.objects;

import com.gvi.project.Components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.helper.TimeoutHelper;
import com.gvi.project.models.entities.Player;


public class OBJ_Boots extends AnimatedObject {
	public OBJ_Boots() {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDungeon", "boots");
		name = "Boots";
		interactHint = "[F] Equip Boots";
		canInteract = true;
		collision = true;
		setUpAnimationComponent();
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

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.isLooping();
		animComp.cycleLength = 0.4;
		animComp.delayBetweenCycles = 2;
		animComp.delay = 0.8;

		sprite = animComp.getCurrentSprite();
	};
}
