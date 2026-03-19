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
import java.util.logging.Logger;

public class SpriteSheet {
	private SpriteSheetConfig config;
	private Image sheetImage;
	private static final Logger logger = Logger.getLogger(SpriteSheet.class.getName());

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
		try {
			SpriteConfig spriteConfig = config.spriteGroups.get(spriteGroupId).sprites.get(spriteId);
			
			// Validiere Konfiguration
			if (spriteConfig == null) {
				throw new IllegalArgumentException("Sprite not found: group=" + spriteGroupId + ", id=" + spriteId);
			}

			int startX = spriteConfig.spriteX * config.spriteSize;
			int startY = spriteConfig.spriteY * config.spriteSize;
			int width = spriteConfig.spriteWidth * config.spriteSize;
			int height = spriteConfig.spriteHeight * config.spriteSize;

			// Überprüfe ob die angeforderten Koordinaten im Bild liegen
			if (startX < 0 || startY < 0 || startX + width > sheetImage.getWidth() || startY + height > sheetImage.getHeight()) {
				logger.warning(String.format(
					"Sprite coordinates out of bounds for %s:%s. Config: x=%d, y=%d, w=%d, h=%d. Image: %dx%d",
					spriteGroupId, spriteId, startX, startY, width, height, 
					(int)sheetImage.getWidth(), (int)sheetImage.getHeight()
				));
				// Erstelle ein leeres Bild als Fallback
				return new WritableImage(width, height);
			}

			PixelReader reader = sheetImage.getPixelReader();
			WritableImage subImage = new WritableImage(reader, startX, startY, width, height);

			return subImage;
		} catch (Exception e) {
			logger.severe("Error loading sprite: group=" + spriteGroupId + ", id=" + spriteId + ". Error: " + e.getMessage());
			e.printStackTrace();
			// Erstelle ein leeres Fallback-Bild
			return new WritableImage(16, 16);
		}
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
