package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardTest {

    @Test
    void fromPercentageShouldHonorRewardBoundaries() {
        assertEquals(Reward.NONE, Reward.fromPercentage(0));
        assertEquals(Reward.NONE, Reward.fromPercentage(58.99));
        assertEquals(Reward.BRONZE, Reward.fromPercentage(59));
        assertEquals(Reward.BRONZE, Reward.fromPercentage(73.99));
        assertEquals(Reward.SILVER, Reward.fromPercentage(74));
        assertEquals(Reward.SILVER, Reward.fromPercentage(88.99));
        assertEquals(Reward.GOLD, Reward.fromPercentage(89));
        assertEquals(Reward.GOLD, Reward.fromPercentage(97.99));
        assertEquals(Reward.GOLD_PERFECT, Reward.fromPercentage(98));
    }

    @Test
    void gettersShouldExposeDisplayNameAndMinimumPercentage() {
        assertEquals("Keine Medaille", Reward.NONE.getDisplayName());
        assertEquals(0, Reward.NONE.getMinPercentage());
        assertEquals("Special", Reward.GOLD_PERFECT.getDisplayName());
        assertEquals(98, Reward.GOLD_PERFECT.getMinPercentage());
    }
}