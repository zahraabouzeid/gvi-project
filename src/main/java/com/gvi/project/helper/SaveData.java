package com.gvi.project.helper;

import java.util.List;

public class SaveData {

    public String playerName;
    public int    worldX, worldY, gridX, gridY;
    public String direction;
    public int    score, healthHalf, maxHealthHalf, playerKeys;
    public String currentMap;
    public String savedAt;
    public List<SavedObject> presentObjects;

    public static class SavedObject {
        public String  className;
        public int     worldX, worldY;
        public boolean quizCompleted;

        public SavedObject() {}

        public SavedObject(String className, int worldX, int worldY, boolean quizCompleted) {
            this.className     = className;
            this.worldX        = worldX;
            this.worldY        = worldY;
            this.quizCompleted = quizCompleted;
        }
    }
}
