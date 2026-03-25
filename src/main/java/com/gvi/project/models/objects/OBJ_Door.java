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
	private boolean isOpen = false;
	private boolean hasConditions = false;
	private ConditionsObject conditions;

	public OBJ_Door(Boolean isOpen, String type, ConditionsObject conditions) {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDoorUp", "door");
		this.isOpen = isOpen;
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
		interactHint = "[F] Unlock %s".formatted(name);
		spriteDirectionUp = true;
		collision = true;
		collisionBox.setWidth(2 * GeneralSettings.getTileSize());
		canInteract = !isOpen;
		visibleInMinimap = !isOpen;
		hasConditions = false;
		setUpAnimationComponent();
	}

	public void setConditions(ConditionsObject conditions) {
		if (conditions == null) return;
		if (conditions.and_or == null || conditions.conditionObjects == null) return;
		this.conditions = conditions;
		hasConditions = true;
	}

	public void runAnimation() {
		AnimationComponent animComp = (AnimationComponent) components.get("Animation");
		if (!isOpen) {
			animComp.playForward();   // öffnen
			isOpen = true;
		} else {
			animComp.playBackward();  // schließen
			isOpen = false;
		}
		sound.setFile(4);
		sound.loop();
		sound.play();
	}

	@Override
	public void onConfirm(GamePanel gp, int objIndex) {
		if (!canInteract) return;

		if (conditions != null) {
			if (!conditionsAreMeet(gp)) return;
			onSuccess(gp);
		}

		runAnimation();
		canInteract = false;
		visibleInMinimap = false;
	}

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.cycleLength = 2;
		animComp.setLooping(false); // Tür ist One-Shot

		animComp.onFinished = () -> {
			this.collision = !isOpen;
			this.canInteract = !isOpen;
			this.visibleInMinimap = !isOpen;
			this.sound.stop();
		};

		if (isOpen) {
			canInteract = false;
			collision = false;
			visibleInMinimap = false;
			animComp.currentFrame = animComp.sprites.size() - 1; // letzter Frame
			sprite = animComp.getCurrentSprite();
		}
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

	@Override
	public void onEventTrigger(){
		runAnimation();
		collision = true;
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
