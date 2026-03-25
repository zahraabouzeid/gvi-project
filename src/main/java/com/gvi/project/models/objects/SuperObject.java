package com.gvi.project.models.objects;

import com.gvi.project.GeneralSettings;
import com.gvi.project.components.Component;
import com.gvi.project.GamePanel;
import com.gvi.project.models.core.Renderable;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.sprite_sheets.Sprite;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

public abstract class SuperObject implements Renderable {

	public Sprite sprite;
	public Boolean spriteDirectionUp = false;
	public String name;
	public String id;
	public boolean collision = false;
	public int worldX, worldY;
	public Rectangle collisionBox = new Rectangle(0, 0, 48, 48);
	public String interactHint = "[F] Interact";
	public final Map<String, Component> components = new HashMap<>();
	public boolean canInteract = false;
	public boolean visibleInMinimap = true;

	public SuperObject() {
		sprite = new Sprite();
	}

	public void onInteract(Player player, GamePanel gp, int objIndex) {
		onConfirm(gp, objIndex);
	}

	public void onConfirm(GamePanel gp, int objIndex) {
		// Default behavior: do nothing
	}

	public void onDestroy() {
		// Default behavior: do nothing
	}

	public void onStep(Player player, GamePanel gp, int objIndex) {
		// Default behavior: do nothing
	}
	public void onEventTrigger(){
		// Default behavior: do nothing
	}

	public void addComponent(Component component){
		this.components.put(component.getComponentId(), component);
	}

	@Override
	public int getY() {
		return worldY;
	}

	@Override
	public void render(GamePanel gp) {
		int tileSize = GeneralSettings.getTileSize();

		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;

		if (worldX + GeneralSettings.getTileSize() > gp.player.worldX - gp.player.screenX &&
			worldX - GeneralSettings.getTileSize() < gp.player.worldX + gp.player.screenX &&
			worldY + GeneralSettings.getTileSize() > gp.player.worldY - gp.player.screenY &&
			worldY - GeneralSettings.getTileSize() < gp.player.worldY + gp.player.screenY) {

			if (spriteDirectionUp) {
				gp.gc.drawImage(sprite.image, screenX + (sprite.imageOffsetX * tileSize), screenY - ((sprite.imageHeight - 1) * tileSize) + (sprite.imageOffsetY * tileSize), tileSize * sprite.imageWidth, tileSize * sprite.imageHeight);
			} else {
				gp.gc.drawImage(sprite.image, screenX + (sprite.imageOffsetX * tileSize), screenY + (sprite.imageOffsetY * tileSize), tileSize * sprite.imageWidth, tileSize * sprite.imageHeight);
			}
		}
	}

	@Override
	public void renderCollisionBox(GamePanel gp){
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;

		if (worldX + GeneralSettings.getTileSize() > gp.player.worldX - gp.player.screenX &&
			worldX - GeneralSettings.getTileSize() < gp.player.worldX + gp.player.screenX &&
			worldY + GeneralSettings.getTileSize() > gp.player.worldY - gp.player.screenY &&
			worldY - GeneralSettings.getTileSize() < gp.player.worldY + gp.player.screenY) {

			if (GeneralSettings.isDevMode() && this.collision) {
				gp.gc.setFill(new Color(1, 0, 0, 0.3));
				gp.gc.fillRect(screenX + collisionBox.getX(), screenY + collisionBox.getY(), collisionBox.getWidth(), collisionBox.getHeight());
			}

			if (GeneralSettings.isDevMode() && !this.collision) {
				gp.gc.setFill(new Color(0, 1, 0, 0.3));
				gp.gc.fillRect(screenX + collisionBox.getX(), screenY + collisionBox.getY(), collisionBox.getWidth(), collisionBox.getHeight());
			}
		}
	}
}
