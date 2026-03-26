package com.gvi.project.models.questions;

/**
 * Calculates the performance reward based on total points achieved.
 * Implements the reward system concept from M6.
 * 
 * Formula: Performance% = (achievedPoints / maxPoints) * 100
 * 
 * Thresholds:
 * - Gold Perfect: >= 98%
 * - Gold:         >= 89% and < 98%
 * - Silver:       >= 74% and < 89%
 * - Bronze:       >= 59% and < 74%
 * - None:         < 59%
 */
public class RewardCalculator {

    private final int maxPoints;
    private final int achievedPoints;

    /**
     * Creates a new RewardCalculator.
     * 
     * @param achievedPoints the total points achieved by the player
     * @param maxPoints the maximum possible points
     */
    public RewardCalculator(int achievedPoints, int maxPoints) {
        this.achievedPoints = Math.max(0, achievedPoints);
        this.maxPoints = Math.max(1, maxPoints); // Avoid division by zero
    }

    /**
     * Calculates the performance percentage.
     * 
     * @return performance as percentage (0.0 to 100.0)
     */
    public double calculatePercentage() {
        return (achievedPoints * 100.0) / maxPoints;
    }

    /**
     * Determines the reward tier based on performance.
     * 
     * @return the appropriate Reward
     */
    public Reward calculateReward() {
        double percentage = calculatePercentage();
        return Reward.fromPercentage(percentage);
    }

    /**
     * Gets the achieved points.
     * @return achieved points
     */
    public int getAchievedPoints() {
        return achievedPoints;
    }

    /**
     * Gets the maximum possible points.
     * @return max points
     */
    public int getMaxPoints() {
        return maxPoints;
    }
}
