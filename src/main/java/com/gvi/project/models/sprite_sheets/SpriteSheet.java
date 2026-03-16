package com.gvi.project.models.sprite_sheets;

import com.gvi.project.helper.ConfigHelper;
import com.gvi.project.models.sprite_sheets.config.SpriteConfig;
import com.gvi.project.models.sprite_sheets.config.SpriteGroupConfig;
import com.gvi.project.models.sprite_sheets.config.SpriteSheetConfig;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpriteSheet {
	private SpriteSheetConfig config;
	private Image sheetImage;

	/**
	 *
	 * @param path Der Pfad zum laden der SheetConfig sowie das dazugehörige SpriteSheet
	 */
	public SpriteSheet(String path) {
		this.config = ConfigHelper.getConfig(SpriteSheetConfig.class, "%s.json".formatted(path));
		this.sheetImage = new Image("%s.png".formatted(path));
	}

	// Isoliert Teilbild vom Spritesheet Bild
	public Image getImage(String spriteGroupId, String spriteId) {

		SpriteConfig spriteConfig = config.spriteGroups.get(spriteGroupId).sprites.get(spriteId);

		PixelReader reader = sheetImage.getPixelReader();
		WritableImage subImage = new WritableImage(reader, spriteConfig.spriteX * config.spriteSize, spriteConfig.spriteY * config.spriteSize, spriteConfig.spriteWidth * config.spriteSize, spriteConfig.spriteHeight * config.spriteSize);

		return subImage;
	}

	public Sprite getSprite(String spriteGroupId, String spriteId) {
		Image image = getImage(spriteGroupId, spriteId);

		SpriteConfig spriteConfig = config.spriteGroups.get(spriteGroupId).sprites.get(spriteId);

		return new Sprite(image, spriteConfig.spriteHeight, spriteConfig.spriteWidth);
	}

	public Map<String, Sprite> getGroupSpritesAsMap(String spriteGroupId) {
		SpriteGroupConfig spriteGroupConfig = config.spriteGroups.get(spriteGroupId);

		Map<String, Sprite> sprites = new HashMap<>();

		for (Map.Entry<String, SpriteConfig> entry : spriteGroupConfig.sprites.entrySet()){
			sprites.put(entry.getKey(), getSprite(spriteGroupId, entry.getKey()));
		}

		return sprites;
	}

	public ArrayList<Sprite> getGroupSpritesAsList(String spriteGroupId) {
		SpriteGroupConfig spriteGroupConfig = config.spriteGroups.get(spriteGroupId);

		ArrayList<Sprite> sprites = new ArrayList<>();

		for (Map.Entry<String, SpriteConfig> entry : spriteGroupConfig.sprites.entrySet()){
			Sprite sprite = getSprite(spriteGroupId, entry.getKey());

			sprites.add(sprite);
		}

		return sprites;
	}

	public SpriteConfig getSpriteConfig(String spriteGroupId, String spriteId) {
		return config.spriteGroups.get(spriteGroupId).sprites.get(spriteId);
	}

	public SpriteSheetConfig getConfig() {
		return config;
	}
}
