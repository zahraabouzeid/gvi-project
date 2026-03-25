package com.gvi.project.models.questions;

/**
 * Calculates scores for different question types according to the reward system concept (M6).
 * 
 * Scoring formulas:
 * - True/False: P = 1 * s
 * - Multiple Choice: P = (2 + n_correct * 1) * s
 * - Fill-in-Blank (Gap): P = (3 + n_gaps * 1) * s
 * 
 * Where s = difficulty multiplier (1=easy, 2=medium, 3=hard)
 * 
 * For incorrect answers, negative points are awarded (same absolute value as positive).
 */
public class ScoreCalculator {

    /**
     * Calculates points for a True/False question.
     * Formula: P = 1 * s
     * 
     * @param difficulty the difficulty level
     * @param correct whether the answer is correct
     * @return positive points if correct, negative if incorrect
     */
    public static int calculateTrueFalsePoints(Difficulty difficulty, boolean correct) {
        int points = 1 * difficulty.getMultiplier();
        return correct ? points : -points;
    }

    /**
     * Calculates points for a Multiple Choice question.
     * Formula: P = (2 + n_correct * 1) * s
     * 
     * @param difficulty the difficulty level
     * @param numberOfCorrectOptions the number of correct answer options
     * @param correct whether the selected answer is correct
     * @return positive points if correct, negative if incorrect
     */
    public static int calculateMultipleChoicePoints(Difficulty difficulty, int numberOfCorrectOptions, boolean correct) {
        int points = (2 + numberOfCorrectOptions * 1) * difficulty.getMultiplier();
        return correct ? points : -points;
    }

    /**
     * Calculates maximum points for a Multiple Choice question (for correct answers).
     * Formula: P = (2 + n_correct * 1) * s
     * 
     * @param difficulty the difficulty level
     * @param numberOfCorrectOptions the number of correct answer options
     * @return the maximum achievable points
     */
    public static int calculateMultipleChoiceMaxPoints(Difficulty difficulty, int numberOfCorrectOptions) {
        return (2 + numberOfCorrectOptions * 1) * difficulty.getMultiplier();
    }

    /**
     * Calculates points for a single gap in a Fill-in-Blank question.
     * Base formula for the entire question: P = (3 + n_gaps * 1) * s
     * For individual gaps, we distribute: base_per_gap = 3/total_gaps, additional = 1
     * 
     * @param difficulty the difficulty level
     * @param totalGaps the total number of gaps in the question
     * @param correct whether the gap was filled correctly
     * @return positive points if correct, negative if incorrect
     */
    public static int calculateGapPoints(Difficulty difficulty, int totalGaps, boolean correct) {
        // For a single gap: distribute base points (3) and add 1 per gap
        // Simplified: each gap is worth approximately equal share of total
        int totalPoints = (3 + totalGaps * 1) * difficulty.getMultiplier();
        int pointsPerGap = Math.max(1, totalPoints / totalGaps);
        return correct ? pointsPerGap : -pointsPerGap;
    }

    /**
     * Calculates maximum points for a Fill-in-Blank question.
     * Formula: P = (3 + n_gaps * 1) * s
     * 
     * @param difficulty the difficulty level
     * @param numberOfGaps the number of gaps/blanks in the question
     * @return the maximum achievable points
     */
    public static int calculateGapMaxPoints(Difficulty difficulty, int numberOfGaps) {
        return (3 + numberOfGaps * 1) * difficulty.getMultiplier();
    }
}
