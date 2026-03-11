package com.gvi.project.systems;

import com.gvi.project.GamePanel;
import com.gvi.project.models.core.Renderable;
import com.gvi.project.models.game_maps.GameMapLayer;
import com.gvi.project.models.sprite_sheets.Sprite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RenderSystem {
	private GamePanel gp;

	public RenderSystem(GamePanel gp) {
		this.gp = gp;
	}

	public void render() {
		renderLayer(gp.currentMap.getLayer("FLOOR"));
		renderLayer(gp.currentMap.getLayer("WALLS"));
//		renderLayer(gp.currentMap.getLayer("DECORATIONS"));

		List<Renderable> dynamic = new ArrayList<>();
		dynamic.addAll(gp.entityList);
		dynamic.addAll(gp.obj);
		dynamic.add(gp.player);

		dynamic.sort(Comparator.comparingInt(Renderable::getY));

		for (Renderable r : dynamic) {
			r.render(gp);
		}

		renderLayer(gp.currentMap.getLayer("CEILING"));

		gp.ui.minimap.draw(gp.gc);
	}

	private void renderLayer(GameMapLayer layer) {

		int worldCol = 0;
		int worldRow = 0;

		while (worldCol < gp.currentMap.width && worldRow < gp.currentMap.height) {
			String spriteKey = layer.layout[worldCol][worldRow];
			Sprite sprite = gp.spriteManager.getStoredSprite(spriteKey);

			int tileSize = gp.generalSettings.tileSize;

			int worldX = worldCol * tileSize;
			int worldY = worldRow * tileSize;

			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;

			int spriteWidth  = tileSize * sprite.imageWidth;
			int spriteHeight = tileSize * sprite.imageHeight;

			int screenLeft   = screenX;
			int screenRight  = screenX + spriteWidth;
			int screenTop    = screenY - (spriteHeight - tileSize);
			int screenBottom = screenY + tileSize;

			if (
				screenRight > 0 &&
				screenLeft < gp.generalSettings.screenWidth &&
				screenBottom > 0 &&
				screenTop < gp.generalSettings.screenHeight)
			{
				int drawX = screenX;
				int drawY = screenY - (sprite.imageHeight - 1) * tileSize;

				gp.gc.drawImage(
						sprite.image,
						drawX,
						drawY,
						spriteWidth,
						spriteHeight
				);
			}

			worldCol++;

			if (worldCol == gp.currentMap.width) {
				worldCol = 0;
				worldRow++;
			}
		}
	}
}
