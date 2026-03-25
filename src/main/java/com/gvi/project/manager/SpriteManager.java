package com.gvi.project.manager;

import com.gvi.project.models.sprite_sheets.Sprite;

import java.util.HashMap;
import java.util.Map;

public class SpriteManager {

	public Map<String, Sprite> sprites = new HashMap<>();

	public SpriteManager() {}

	public void registerSprite(String key, Sprite sprite) {
		if (!sprites.containsKey(key)) {
			sprites.put(key, sprite);
		}
	}

	public Sprite getRegisterdSprite(String key) {
		return sprites.get(key);
	}

	public void reset(){
		if (sprites != null){
			sprites.clear();
		}
	}
}
