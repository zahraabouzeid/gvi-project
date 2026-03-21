package com.gvi.project.models.objects;

import com.gvi.project.components.AnimationComponent;

public abstract class AnimatedObject extends SuperObject{

	public AnimatedObject(String sheetPath, String spriteGroupId) {
		AnimationComponent animComp = new AnimationComponent(sheetPath, spriteGroupId);
		components.put("Animation", animComp);
		this.sprite = animComp.getCurrentSprite();
	}

	public abstract void setUpAnimationComponent();
}
