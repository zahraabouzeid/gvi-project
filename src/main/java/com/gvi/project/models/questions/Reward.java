package com.gvi.project.models.questions;

/**
 * Represents the reward tier (medal) based on performance.
 * According to M6 concept: Bronze, Silver, Gold based on percentage thresholds.
 */
public enum Reward {
    NONE("Keine Medaille", 0),
    BRONZE("Bronze", 59),
    SILVER("Silber", 74),
    GOLD("Gold", 89),
    GOLD_PERFECT("Gold Perfect", 98);

    private final String displayName;
    private final int minPercentage;

    Reward(String displayName, int minPercentage) {
        this.displayName = displayName;
        this.minPercentage = minPercentage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinPercentage() {
        return minPercentage;
    }

    /**
     * Determines the reward tier based on performance percentage.
     * 
     * @param percentage the performance percentage (0-100)
     * @return the appropriate Reward tier
     */
    public static Reward fromPercentage(double percentage) {
        if (percentage >= GOLD_PERFECT.minPercentage) {
            return GOLD_PERFECT;
        } else if (percentage >= GOLD.minPercentage) {
            return GOLD;
        } else if (percentage >= SILVER.minPercentage) {
            return SILVER;
        } else if (percentage >= BRONZE.minPercentage) {
            return BRONZE;
        } else {
            return NONE;
        }
    }
}
