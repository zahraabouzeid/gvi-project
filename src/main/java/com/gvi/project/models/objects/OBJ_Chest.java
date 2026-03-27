package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.Sound;
import com.gvi.project.components.AnimationComponent;
import com.gvi.project.helper.ConditionResult;
import com.gvi.project.helper.ConditionsHelper;

import com.gvi.project.models.data_objects.ConditionObject;
import com.gvi.project.models.data_objects.ConditionsObject;

import java.util.Objects;

public class OBJ_Chest extends AnimatedObject {
	Sound sound = new Sound();
	private boolean hasConditions = false;
	private ConditionsObject conditions;

	public OBJ_Chest(String type, ConditionsObject conditions) {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDungeon", "chest_brown");

		setSpritesForType(type);
		setDefaultValues();
		setConditions(conditions);
	}
	private void setDefaultValues(){
		name = "Chest";
		interactHint = "[F] Unlock Chest";
		canInteract = true;
		hasConditions = false;
		collision = true;
		spriteDirectionUp = true;
		setUpAnimationComponent();
	}

	private void setSpritesForType(String type) {
		switch (type) {
			case "chest_red":
				setAnimationSprites("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDungeon", "chest_red");
				id = type;
				break;
			case "chest_brown":
				id = type;
				break;
		}
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
			onSuccess(gp, objIndex);
		}
		// Truhe geöffnet - Berechne Belohnung basierend auf Gesamtpunkten
		gp.ui.checkRewardThreshold(gp);
	}
	
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.cycleLength = 1;
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

	private void onSuccess(GamePanel gp, int objIndex){
		if("and".equals(conditions.and_or)){
			for (ConditionObject condition : conditions.conditionObjects) {
				if(Objects.equals(condition.type, "item")){
					switch (condition.onSuccess) {
						case "remove":
							gp.player.removeItems(condition.compareWith, condition.value);
							this.canInteract = false;
							break;
						default:
							break;
					}
				}
			}
		}

		AnimationComponent animComp = (AnimationComponent) components.get("Animation");
		animComp.onFinished = () -> {
			this.collision = false;
			spawnItem(gp, objIndex);
		};
		animComp.trigger();
		sound.setFile(3);
		sound.play();
	}

	private void spawnItem(GamePanel gp, int objIndex) {
		String chestType = id.substring(0, id.lastIndexOf("_"));

		SuperObject obj = switch (chestType) {
			case "chest_brown" -> new OBJ_Boots();
			case "chest_red" -> new OBJ_HealingPotion();
			default -> null;
		};

		if (obj == null) throw new NullPointerException("Item with id " + this.id + " not found");
		gp.obj.remove(objIndex);
		obj.worldX = this.worldX;
		obj.worldY = this.worldY;
		gp.obj.add(objIndex, obj);
	}
}
