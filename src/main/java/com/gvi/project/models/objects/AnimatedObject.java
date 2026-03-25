package com.gvi.project.models.objects;

import com.gvi.project.components.AnimationComponent;
import com.gvi.project.models.sprite_sheets.SpriteSheet;

public abstract class AnimatedObject extends SuperObject{

	public AnimatedObject(String sheetPath, String spriteGroupId) {
		AnimationComponent animComp = new AnimationComponent(sheetPath, spriteGroupId);
		components.put("Animation", animComp);
		this.sprite = animComp.getCurrentSprite();
	}

	public abstract void setUpAnimationComponent();

	public void setAnimationSprites(String sheetPath, String spriteGroupId){
		SpriteSheet sheet = new SpriteSheet(sheetPath);
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.sprites = sheet.getGroupSpritesAsList(spriteGroupId);
	}
}
