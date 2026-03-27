package com.gvi.project.helper;

import java.util.List;
import java.util.Map;

public class SaveData {

    public String playerName;
    public int    worldX, worldY, gridX, gridY;
    public String direction;
    public int    score, healthHalf, maxHealthHalf;
    public double playtime;
    public Map<String, Integer> playerItems;
    public String currentMap;
    public String savedAt;
    public Map<String, List<SavedObject>> allMapObjects;

    public static class SavedObject {
        public String        className;
        public String        objectId;
        public int           worldX, worldY;
        public boolean       quizCompleted;
        public boolean       doorOpen;
        public boolean       triggered;
        public List<Integer> answeredQuestionIds;

        public SavedObject() {}

        public SavedObject(String className, String objectId, int worldX, int worldY,
                           boolean quizCompleted, boolean doorOpen, boolean triggered,
                           List<Integer> answeredQuestionIds) {
            this.className           = className;
            this.objectId            = objectId;
            this.worldX              = worldX;
            this.worldY              = worldY;
            this.quizCompleted       = quizCompleted;
            this.doorOpen            = doorOpen;
            this.triggered           = triggered;
            this.answeredQuestionIds = answeredQuestionIds;
        }
    }
}
