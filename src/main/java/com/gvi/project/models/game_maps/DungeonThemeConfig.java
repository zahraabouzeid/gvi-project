package com.gvi.project.models.game_maps;

import com.gvi.project.models.questions.TopicArea;
import java.util.Arrays;
import java.util.List;

/**
 * Konfiguration für die Zuordnung von Dungeons zu Themenbereichen.
 * Jeder Dungeon hat zugehörige Lernthemen.
 */
public enum DungeonThemeConfig {
    DUNGEON_MAP_01(
            GameMaps.MAP_01,
            "Datenbank & SQL",
            "Datenbank - SQL und Datenbanken Modellierung",
            Arrays.asList(
                    TopicArea.DATENBANK_SQL,
                    TopicArea.DATENBANKEN_MODELLIERUNG
            )
    ),
    DUNGEON_MAP_02(
            GameMaps.MAP_02,
            "Programmierung & KI",
            "Programmierung Pseudocode und Maschinelles Lernen",
            Arrays.asList(
                    TopicArea.PROGRAMMIERUNG_PSEUDOCODE,
                    TopicArea.MASCHINELLES_LERNEN
            )
    ),
    DUNGEON_MAP_03(
            GameMaps.MAP_03,
            "Business & Recht",
            "Wirtschaft, Recht und UML",
            Arrays.asList(
                    TopicArea.WIRTSCHAFT,
                    TopicArea.RECHT,
                    TopicArea.UML
            )
    );

    private final GameMaps gameMaps;
    private final String title;
    private final String description;
    private final List<TopicArea> topicAreas;

    DungeonThemeConfig(GameMaps gameMaps, String title, String description, List<TopicArea> topicAreas) {
        this.gameMaps = gameMaps;
        this.title = title;
        this.description = description;
        this.topicAreas = topicAreas;
    }

    public GameMaps getGameMaps() {
        return gameMaps;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<TopicArea> getTopicAreas() {
        return topicAreas;
    }

    /**
     * Gibt die Theme-Konfiguration für eine bestimmte Map zurück
     */
    public static DungeonThemeConfig getConfigForMap(GameMaps map) {
        for (DungeonThemeConfig config : values()) {
            if (config.gameMaps == map) {
                return config;
            }
        }
        return null;
    }

    /**
     * Gibt eine Texteingabe für die Dungeon-Themen aus
     */
    public String getThemeDisplayText() {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n\n");
        sb.append(description).append("\n\n");
        sb.append("Themen: ");
        for (int i = 0; i < topicAreas.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(topicAreas.get(i).getDisplayName());
        }
        return sb.toString();
    }
}
