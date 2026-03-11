package com.gvi.project.models.game_maps;

import com.gvi.project.models.objects.SuperObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {
	public int Id;
	public String name;
	public final int width, height;
	public Map<String, GameMapLayer> gameMapLayers;
	public List<SuperObject> objects = new ArrayList<>();

	public GameMap( int id, String name, int width, int height ){
		this.gameMapLayers = new HashMap<>();
		this.Id = id;
		this.name = name;
		this.width = width;
		this.height = height;
	}

	public void addLayer(String renderId,  GameMapLayer gameMapLayer){
		this.gameMapLayers.put(renderId, gameMapLayer);
	}

	public GameMapLayer getLayer(String renderId){
		return this.gameMapLayers.get(renderId);
	}
}
