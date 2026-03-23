package com.gvi.project.models.questions;

/**
 * Represents the difficulty level of a question.
 * Used as multiplier in score calculation according to the reward system concept.
 */
public enum Difficulty {
    EASY(1),      // leicht
    MEDIUM(2),    // mittel
    HARD(3);      // schwer

    private final int multiplier;

    Difficulty(int multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Returns the multiplier factor for score calculation.
     * @return the difficulty multiplier (1, 2, or 3)
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * Converts a database points value to a difficulty level.
     * Maps: 1 -> EASY, 2 -> MEDIUM, 3+ -> HARD
     * @param points the points value from the database
     * @return the corresponding difficulty level
     */
    public static Difficulty fromPoints(int points) {
        if (points <= 1) {
            return EASY;
        } else if (points == 2) {
            return MEDIUM;
        } else {
            return HARD;
        }
    }
}
