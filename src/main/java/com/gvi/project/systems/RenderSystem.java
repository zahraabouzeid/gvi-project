package com.gvi.project.systems;

import com.gvi.project.GamePanel;
import com.gvi.project.models.core.Renderable;
import com.gvi.project.models.game_maps.GameMapLayer;

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

			int worldX = worldCol * gp.generalSettings.tileSize;
			int worldY = worldRow * gp.generalSettings.tileSize;
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;

			if (worldX + gp.generalSettings.tileSize > gp.player.worldX - gp.player.screenX &&
					worldX - gp.generalSettings.tileSize < gp.player.worldX + gp.player.screenX &&
					worldY + gp.generalSettings.tileSize > gp.player.worldY - gp.player.screenY &&
					worldY - gp.generalSettings.tileSize < gp.player.worldY + gp.player.screenY) {
				gp.gc.drawImage(gp.spriteManager.getSprite(spriteKey).image, screenX, screenY, gp.generalSettings.tileSize, gp.generalSettings.tileSize);
			}

			worldCol++;

			if (worldCol == gp.currentMap.width) {
				worldCol = 0;
				worldRow++;
			}
		}
	}
}
