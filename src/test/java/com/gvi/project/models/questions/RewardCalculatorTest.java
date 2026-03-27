package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardCalculatorTest {

    @Test
    void constructorShouldClampInvalidInputs() {
        RewardCalculator calculator = new RewardCalculator(-5, 0);

        assertEquals(0, calculator.getAchievedPoints());
        assertEquals(1, calculator.getMaxPoints());
        assertEquals(0.0, calculator.calculatePercentage());
        assertEquals(Reward.NONE, calculator.calculateReward());
    }

    @Test
    void calculateRewardShouldUseConfiguredThresholds() {
        assertEquals(Reward.NONE, new RewardCalculator(58, 100).calculateReward());
        assertEquals(Reward.BRONZE, new RewardCalculator(59, 100).calculateReward());
        assertEquals(Reward.SILVER, new RewardCalculator(74, 100).calculateReward());
        assertEquals(Reward.GOLD, new RewardCalculator(89, 100).calculateReward());
        assertEquals(Reward.GOLD_PERFECT, new RewardCalculator(98, 100).calculateReward());
    }

    @Test
    void calculatePercentageShouldPreserveFractionalScores() {
        RewardCalculator calculator = new RewardCalculator(37, 50);

        assertEquals(74.0, calculator.calculatePercentage());
        assertEquals(Reward.SILVER, calculator.calculateReward());
    }
}