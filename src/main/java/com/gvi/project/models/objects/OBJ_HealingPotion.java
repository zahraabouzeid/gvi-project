package com.gvi.project.models.objects;

import com.gvi.project.Components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.models.entities.Player;


public class OBJ_HealingPotion extends AnimatedObject {
	public OBJ_HealingPotion(){
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDungeon", "potion_red");
		name = "Healing Potion";
		canInteract = true;
		interactHint = "[F] Use healing potion";
		collision = true;
		setUpAnimationComponent();
	}

	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		if (gp.player.healthHalf == gp.player.maxHealthHalf) return;

		gp.player.healthHalf = Math.min(gp.player.healthHalf + 2, gp.player.maxHealthHalf);
		gp.playSE(2);
		gp.obj.remove(objIndex);
	}

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.isLooping();
		animComp.cycleLength = 0.4;
		animComp.delayBetweenCycles = 2;

		sprite = animComp.getCurrentSprite();
	};
}
