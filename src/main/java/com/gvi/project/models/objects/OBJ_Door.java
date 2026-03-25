package com.gvi.project.models.objects;

import com.gvi.project.GeneralSettings;
import com.gvi.project.components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.Sound;
import com.gvi.project.helper.ConditionResult;
import com.gvi.project.helper.ConditionsHelper;
import com.gvi.project.models.data_objects.ConditionObject;
import com.gvi.project.models.data_objects.ConditionsObject;

import java.util.Objects;

public class OBJ_Door extends AnimatedObject {
	Sound sound = new Sound();
	public boolean isOpen = false;
	public boolean canClose = false;
	private boolean hasConditions = false;
	private ConditionsObject conditions;

	public OBJ_Door(String type, ConditionsObject conditions) {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDoorUp", "door");
		setSpritesForType(type);
		setDefaultValues();
		setConditions(conditions);
	}

	private void setSpritesForType(String type) {
		switch (type) {
			case "double_door":
				setAnimationSprites("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectBigdoor", "door");
			break;
			case "drop_gate":
				break;
		}
	}

	private void setDefaultValues(){
		name = "door";
		id = name + ("_horizontal");
		interactHint = "[F] Unlock %s".formatted(name);
		spriteDirectionUp = true;
		collision = true;
		collisionBox.setWidth(2 * GeneralSettings.getTileSize());
		canInteract = true;
		hasConditions = false;
		setUpAnimationComponent();
	}

	public void setConditions(ConditionsObject conditions) {
		if (conditions == null) return;
		if (conditions.and_or == null || conditions.conditionObjects == null) return;
		this.conditions = conditions;
		hasConditions = true;
	}

	@Override
	public void onConfirm(GamePanel gp, int objIndex) {
		if (!canInteract) return;
		if (conditions != null) {
			if (!conditionsAreMeet(gp)) return;
			onSuccess(gp);
		}
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
					result.getMessage()
			);
		}
		return result.success ;
	}

	private void onSuccess(GamePanel gp){
		if("and".equals(conditions.and_or)){
			for (ConditionObject condition : conditions.conditionObjects) {
				if(Objects.equals(condition.type, "item")){
					switch (condition.onSuccess) {
						case "remove":
							gp.player.removeItems(condition.compareWith, condition.value);
							break;
						default:
							break;
					}
				}
			}
		}
	}
}
