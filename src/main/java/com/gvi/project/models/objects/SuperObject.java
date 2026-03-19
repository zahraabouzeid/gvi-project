package com.gvi.project.models.objects;

import com.gvi.project.Components.Component;
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
	public boolean collision = false;
	public int worldX, worldY;
	public Rectangle collisionBox = new Rectangle(0, 0, 48, 48);
	public String interactHint = "[F] Interact";
	public final Map<String, Component> components = new HashMap<>();
	public boolean canInteract = false;

	public SuperObject() {
		sprite = new Sprite();
	}

	public void onInteract(Player player, GamePanel gp, int objIndex) {
		onConfirm(player, gp, objIndex);
	}

	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		// Default behavior: do nothing
	}

	public void onStep(Player player, GamePanel gp, int objIndex) {
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
		int tileSize = gp.generalSettings.tileSize;

		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;

		if (worldX + gp.generalSettings.tileSize > gp.player.worldX - gp.player.screenX &&
			worldX - gp.generalSettings.tileSize < gp.player.worldX + gp.player.screenX &&
			worldY + gp.generalSettings.tileSize > gp.player.worldY - gp.player.screenY &&
			worldY - gp.generalSettings.tileSize < gp.player.worldY + gp.player.screenY) {

			if (spriteDirectionUp) {
				gp.gc.drawImage(sprite.image, screenX, screenY - (sprite.imageHeight - 1) * tileSize, tileSize * sprite.imageWidth, tileSize * sprite.imageHeight);
			} else {
				gp.gc.drawImage(sprite.image, screenX, screenY, tileSize * sprite.imageWidth, tileSize * sprite.imageHeight);
			}
		}
	}

	@Override
	public void renderCollisionBox(GamePanel gp){
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;

		if (worldX + gp.generalSettings.tileSize > gp.player.worldX - gp.player.screenX &&
			worldX - gp.generalSettings.tileSize < gp.player.worldX + gp.player.screenX &&
			worldY + gp.generalSettings.tileSize > gp.player.worldY - gp.player.screenY &&
			worldY - gp.generalSettings.tileSize < gp.player.worldY + gp.player.screenY) {

			if (gp.generalSettings.isDevMode && this.collision) {
				gp.gc.setFill(new Color(1, 0, 0, 0.3));
				gp.gc.fillRect(screenX + collisionBox.getX(), screenY + collisionBox.getY(), collisionBox.getWidth(), collisionBox.getHeight());
			}

			if (gp.generalSettings.isDevMode && !this.collision) {
				gp.gc.setFill(new Color(0, 1, 0, 0.3));
				gp.gc.fillRect(screenX + collisionBox.getX(), screenY + collisionBox.getY(), collisionBox.getWidth(), collisionBox.getHeight());
			}
		}
	}
}
