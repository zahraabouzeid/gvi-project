package com.gvi.project.models.objects;

import com.gvi.project.Components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.models.entities.Player;


public class OBJ_Key extends AnimatedObject {

	public OBJ_Key(KeyTyp typ) {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDungeon", typ.getSpriteGroupID());
		name = typ.getName();
		canInteract = true;
		interactHint = "[F] Pick up %s Key".formatted(name);
		collision = true;
		setUpAnimationComponent();
	}

	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		gp.playSE(1);

		//gp.player.addItem(name);

		gp.obj.remove(objIndex);
		gp.ui.openMessage("You got a %s key!".formatted(name));
	}

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.isLooping();
		animComp.cycleLength = 0.5;
		animComp.delayBetweenCycles = 0.8;

		sprite = animComp.getCurrentSprite();
	}
}
