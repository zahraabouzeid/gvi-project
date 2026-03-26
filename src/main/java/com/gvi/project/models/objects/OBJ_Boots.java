package com.gvi.project.models.objects;

import com.gvi.project.components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.helper.TimeoutHelper;


public class OBJ_Boots extends AnimatedObject {
	public OBJ_Boots() {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDungeon", "boots");
		name = "boots";
		id = name;
		interactHint = "[F] Equip %s".formatted(name);
		canInteract = true;
		collision = true;
		setUpAnimationComponent();
	}

	@Override
	public void onConfirm(GamePanel gp, int objIndex) {
		gp.playSE(2);
		gp.player.speed = 8;
		gp.obj.remove(objIndex);
		gp.ui.openMessage("SPEED UP!");
		TimeoutHelper.setTimeout(() -> {
			gp.player.speed = 4;
		}, 10000);
	}

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.setLooping(true);
		animComp.cycleLength = 0.4;
		animComp.delayBetweenCycles = 2;
		animComp.delay = 0.8;

		animComp.triggerLoop();
	}
}
