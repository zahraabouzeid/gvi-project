package com.gvi.project.models.objects;

import com.gvi.project.models.game_maps.config.GameObjectConfig;
import com.gvi.project.models.questions.TopicArea;

import java.util.Map;

public class ObjectFactory {

	public static SuperObject create(GameObjectConfig config) {
		var data = config.data != null ? config.data : Map.<String, String>of();
		SuperObject object;

		switch (config.objectType) {
			case "QUIZ_STATION":
				object = new OBJ_QuizStation(
						TopicArea.valueOf(data.get("topicArea")),
						data.getOrDefault("spriteGroup", "crystal_blue")
				);
				break;
			case "MAP_CHANGE_TRIGGER":
				object = new OBJ_MapChangeTrigger(
						data.getOrDefault("player_direction", "down"),
						data.getOrDefault("direction", "up"),
						Integer.parseInt(data.getOrDefault("map_id", "0")),
						Integer.parseInt(data.getOrDefault("target_map_spawn_location_x", "0")),
						Integer.parseInt(data.getOrDefault("target_map_spawn_location_y", "0"))
				);
				break;
			case "BOOTS":
				object = new OBJ_Boots();
				break;
			case "DOOR":
				object = new OBJ_Door(data.getOrDefault("isOpen", "false").equals("true"), data.getOrDefault("type", "drop_gate"), config.conditions);
				break;
			case "CHEST":
				object = new OBJ_Chest( data.getOrDefault("type", "chest_brown"), config.conditions);
				break;
			case "HEALING_POTION":
				object = new OBJ_HealingPotion();
				break;
			case "WOOD_COLUMN":
				object = new OBJ_Wood_Column();
				break;
			case "EVENT_TRIGGER":
				OBJ_EventTrigger eventTrigger = new OBJ_EventTrigger(data.getOrDefault("targetObject", "????"));
				eventTrigger.setDimensions(Integer.parseInt(data.getOrDefault("width", "1")), Integer.parseInt(data.getOrDefault("height", "1")));
				object = eventTrigger;
				break;
			default:
				object = null;
		}
		return object;
	}
}
