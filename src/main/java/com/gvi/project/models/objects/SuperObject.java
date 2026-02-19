package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.models.entities.Player;
import javafx.scene.image.Image;

import java.awt.*;

public abstract class SuperObject {

	public Image image;
	public String name;
	public boolean collision = false;
	public int worldX, worldY;
	public Rectangle collisionBox = new Rectangle(0, 0, 48, 48);
	public int collisionBoxDefaultX = 0;
	public int collisionBoxDefaultY = 0;

	public String interactHint = "[F] Interact";

	public void onInteract(Player player, GamePanel gp, int objIndex) {
		onConfirm(player, gp, objIndex);
	}

	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		// Default behavior: do nothing
	}

	public void draw(GamePanel gp) {
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;

		if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
			worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
			worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
			worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
			gp.gc.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize);
		}
	}
}
