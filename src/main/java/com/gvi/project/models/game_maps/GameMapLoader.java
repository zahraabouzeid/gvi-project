package com.gvi.project.models.game_maps;

import com.gvi.project.GamePanel;
import com.gvi.project.helper.ConfigHelper;
import com.gvi.project.models.game_maps.config.*;
import com.gvi.project.models.objects.ObjectFactory;
import com.gvi.project.models.objects.SuperObject;
import com.gvi.project.models.sprite_sheets.Sprite;
import com.gvi.project.models.sprite_sheets.SpriteSheet;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMapLoader {
	private GamePanel gp;

	Map<String, SpriteSheet> spriteSheets;

	public GameMapLoader(GamePanel gp) {
		this.gp = gp;
		this.spriteSheets = new HashMap<>();
	}

	public GameMap loadMap(String filename) {
		gp.spriteManager.reset();
		GameMapConfig config;

		config = ConfigHelper.getConfig(GameMapConfig.class, "/maps/%s".formatted(filename));
		return buildMap(config);
	}

	private GameMap buildMap (GameMapConfig config) {
		GameMap map = new GameMap(
				config.id,
				config.name,
				config.width,
				config.height
		);

		gp.cChecker.collisionMap = new boolean[config.width][config.height];

		addGameMapLayers(map, config);
		initMapObjects(config);

		return map;
	}


	private void addGameMapLayers(GameMap map, GameMapConfig config) {
		for (GameMapLayerConfig layerConfig : config.layers){
			GameMapLayer layer = new GameMapLayer(config.width, config.height);

			parseSpriteInformation(layer, layerConfig, config.width, config.height);

			map.addLayer(layerConfig.renderId, layer);
		}
	}

	private void initMapObjects(GameMapConfig config){
		if (config.objects == null) return;
		for (GameObjectConfig objectConfig : config.objects){
			SuperObject obj = ObjectFactory.create(objectConfig);
			obj.worldX = objectConfig.x * gp.generalSettings.tileSize;
			obj.worldY = objectConfig.y * gp.generalSettings.tileSize;
			gp.obj.add(obj);
		}
	}

	// Iteriert über Layout und ersetzt sprite index mit sprite Key des dazugehörigen bereits registrieten Sprites
	
	private void parseSpriteInformation(GameMapLayer layer, GameMapLayerConfig layerConfig, int width, int height){
		List<String> usedSpriteKeys = registerUsedSprites(layerConfig.usedSpriteSheets);

		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				int spriteId = layerConfig.spriteLayout[y][x];

				if (spriteId == 999) {
					layer.layout[x][y] = "empty";
					continue;
				};

				if (spriteId == 998) {
					layer.layout[x][y] = "blocker";

					// Setze Collision als true in collision Map
					gp.cChecker.collisionMap[x][y] = true;
					continue;
				};

				String spriteKey = usedSpriteKeys.get(spriteId);

				layer.layout[x][y] = spriteKey;

				// Setze Collision als true in collision Map
				gp.cChecker.collisionMap[x][y] = spriteKey.charAt(0) == '1' && !gp.cChecker.collisionMap[x][y];
			}
		}
	}

	private List<String> registerUsedSprites(GameMapSpriteSheetConfig[] sheetConfigs) {
		List<String> spriteKeys = new ArrayList<>();
		registerBasicSprites();

		for (GameMapSpriteSheetConfig sheetConfig : sheetConfigs) {
			//Überprüfen ob SpriteSheet geladen wurde. Wenn nicht lade SpriteSheet und speicher es in spriteSheets Map
			if (!this.spriteSheets.containsKey(sheetConfig.fileName)){
				spriteSheets.put(sheetConfig.fileName, new SpriteSheet(sheetConfig.filePath + sheetConfig.fileName));
			}

			SpriteSheet spriteSheet = spriteSheets.get(sheetConfig.fileName);

			//Iteriere über Spritegruppen, isoliere Teilbilder und registriere Sprites in SpriteManager
			for (GameMapSpriteConfig usedSprite : sheetConfig.usedSprites){

				Sprite sprite = new Sprite();
				sprite.image = spriteSheet.getImage(usedSprite.spriteGroup, usedSprite.spriteId);
				sprite.imageHeight = spriteSheet.getSpriteConfig(usedSprite.spriteGroup, usedSprite.spriteId).spriteHeight;
				sprite.imageWidth = spriteSheet.getSpriteConfig(usedSprite.spriteGroup, usedSprite.spriteId).spriteWidth;
				sprite.hasCollision = usedSprite.hasCollision;

				String key = "%s:%s:%s:%s".formatted(usedSprite.hasCollision ? "1": "0",sheetConfig.fileName, usedSprite.spriteGroup, usedSprite.spriteId);

				this.gp.spriteManager.registerSprite(key, sprite);
				spriteKeys.add(key);
			}
		}

		return spriteKeys;
	}

	private void registerBasicSprites(){
		this.gp.spriteManager.registerSprite("empty", new Sprite(new WritableImage(gp.generalSettings.tileSize, gp.generalSettings.tileSize),1,1,false));
		this.gp.spriteManager.registerSprite("blocker", new Sprite(new WritableImage(gp.generalSettings.tileSize, gp.generalSettings.tileSize),1,1,true));
	}
}
