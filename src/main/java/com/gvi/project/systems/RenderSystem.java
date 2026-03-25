package com.gvi.project.systems;

import com.gvi.project.GamePanel;
import com.gvi.project.GeneralSettings;
import com.gvi.project.models.core.Renderable;
import com.gvi.project.models.game_maps.GameMapLayer;
import com.gvi.project.models.sprite_sheets.Sprite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RenderSystem {
	private final GamePanel gp;

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

		for (Renderable r : dynamic) {
			r.renderCollisionBox(gp);
		}

		gp.ui.minimap.draw(gp.gc);
	}

	private void renderLayer(GameMapLayer layer) {

		for (int worldRow = 0; worldRow < gp.currentMap.height; worldRow++) {
			for (int worldCol = 0; worldCol < gp.currentMap.width; worldCol++) {
				String spriteKey = layer.layout[worldCol][worldRow];
				Sprite sprite = gp.spriteManager.getRegisterdSprite(spriteKey);

				int tileSize = GeneralSettings.getTileSize();

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
					screenLeft < GeneralSettings.getScreenWidth() &&
					screenBottom > 0 &&
					screenTop < GeneralSettings.getScreenHeight())
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
			}
		}
	}
}
