package com.gvi.project.systems;

import com.gvi.project.components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.models.objects.SuperObject;

public class AnimationSystem {

	private final GamePanel gp;

	public AnimationSystem(GamePanel gp) {
		this.gp = gp;
	}

	public void tick(double delta) {
		for (SuperObject obj : gp.obj) {
			if (obj == null) continue;
			updateSprite(obj, delta);
		}

		for (SuperObject obj : gp.hudObj) {
			if (obj == null) continue;
			updateSprite(obj, delta);
		}
	}

	private void updateSprite(SuperObject obj, double delta) {

		if (obj.components.containsKey("Animation")) {

			AnimationComponent anim = (AnimationComponent) obj.components.get("Animation");
			anim.update(delta);
			obj.sprite = anim.getCurrentSprite();
		}
	}
}