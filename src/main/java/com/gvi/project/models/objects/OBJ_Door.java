package com.gvi.project.models.objects;

import com.gvi.project.components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.Sound;
import com.gvi.project.helper.ConditionResult;
import com.gvi.project.helper.ConditionsHelper;
import com.gvi.project.models.data_objects.ConditionsObject;

import java.util.List;

public class OBJ_Door extends AnimatedObject {
	Sound sound = new Sound();
	public boolean isOpen = false;
	public boolean closable = false;
	private boolean hasConditions = false;
	private ConditionsObject conditions;

	public OBJ_Door() {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDoorUp", "door");
		setDefaultValues();
	}

	public OBJ_Door(ConditionsObject conditions) {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDoorUp", "door");
		setDefaultValues();
		if(conditions != null) {
			setConditions(conditions);
		}
	}

	private void setDefaultValues(){
		name = "door";
		id = name;
		interactHint = "[F] Unlock %s".formatted(name);
		spriteDirectionUp = true;
		collision = true;
		collisionBox.setWidth(2 * 16 * 3);
		canInteract = true;
		hasConditions = false;
		setUpAnimationComponent();
	}

	public void setConditions(ConditionsObject conditions) {
		this.conditions = conditions;
		hasConditions = true;
	}

	@Override
	public void onConfirm(GamePanel gp, int objIndex) {
		if (!canInteract) return;
		if (!conditionsAreMeet(gp)) return;
		AnimationComponent animComp = (AnimationComponent) components.get("Animation");
		animComp.trigger();
		sound.setFile(4);
		sound.loop();
		sound.play();
		isOpen = true;
		canInteract = false;
	}

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.cycleLength = 2;
		animComp.onFinished = () -> {
			this.collision = false;
			this.sound.stop();
		};
	}

	@Override
	public void onDestroy() {
		if(sound != null && sound.isRunning()) sound.stop();
	}

	private boolean conditionsAreMeet(GamePanel gp){
		if (!hasConditions) return true;

		ConditionResult result = ConditionsHelper.conditionsAreMet(gp, conditions);

		if (!result.success) {
			gp.ui.openMessage(
					result.message
			);
		}
		return result.success ;
	}
}
