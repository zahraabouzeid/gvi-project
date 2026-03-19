package com.gvi.project.models.objects;

import com.gvi.project.Components.AnimationComponent;

public abstract class AnimatedObject extends SuperObject{

	public AnimatedObject(String sheetPath, String spriteGroupId) {
		this.addComponent(new AnimationComponent(sheetPath, spriteGroupId));
	}

	public abstract void setUpAnimationComponent();
}
