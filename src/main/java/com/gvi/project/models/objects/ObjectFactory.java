package com.gvi.project.models.objects;

import com.gvi.project.models.game_maps.config.GameObjectConfig;
import com.gvi.project.models.questions.TopicArea;

import java.util.Map;

public class ObjectFactory {

	public static SuperObject create(GameObjectConfig config) {
		var data = config.data != null ? config.data : Map.<String, String>of();

		return switch (config.objectId) {
			case "QUIZ_STATION" -> new OBJ_QuizStation(TopicArea.valueOf(data.get("topicArea")));
			case "MAP_CHANGE_TRIGGER" -> new OBJ_MapChangeTrigger(
					data.getOrDefault("direction", "up"),
					Integer.parseInt(data.getOrDefault("map_id", "0")),
					Integer.parseInt(data.getOrDefault("target_map_spawn_location_x", "0")),
					Integer.parseInt(data.getOrDefault("target_map_spawn_location_y", "0")));
			case "BOOTS" -> new OBJ_Boots();
			case "DOOR" -> new OBJ_Door();
			default -> throw new IllegalArgumentException("Unknown objectId: " + config.objectId);
		};
	}
}
