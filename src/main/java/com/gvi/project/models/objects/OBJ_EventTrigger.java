package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.GeneralSettings;
import com.gvi.project.models.entities.Player;

public class OBJ_EventTrigger extends SuperObject {
	private String targetId;
	private boolean triggered;
	public OBJ_EventTrigger(String targetId) {
		this.targetId = targetId;
		this.visibleInMinimap = false;
	}

	public void setDimensions(int width,int height){
		this.collisionBox.setHeight(height * GeneralSettings.getTileSize());
		this.collisionBox.setWidth(width * GeneralSettings.getTileSize());
	}




	
	public boolean isTriggered() {
		return triggered;
	}

	public void setTriggered() {
		this.triggered = true;
	}

	@Override
	public void onStep(Player player, GamePanel gp, int objIndex) {
		for (SuperObject obj : gp.obj){
			if(obj.id.equals(targetId)){
				if (!triggered){
					obj.onEventTrigger();
					triggered = true;
				}
			}
		}
	}
}
