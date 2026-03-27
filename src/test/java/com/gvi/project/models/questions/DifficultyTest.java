package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DifficultyTest {

    @Test
    void getMultiplierShouldMatchConfiguredValue() {
        assertEquals(1, Difficulty.EASY.getMultiplier());
        assertEquals(2, Difficulty.MEDIUM.getMultiplier());
        assertEquals(3, Difficulty.HARD.getMultiplier());
    }

    @Test
    void fromPointsShouldMapThresholds() {
        assertEquals(Difficulty.EASY, Difficulty.fromPoints(-5));
        assertEquals(Difficulty.EASY, Difficulty.fromPoints(1));
        assertEquals(Difficulty.MEDIUM, Difficulty.fromPoints(2));
        assertEquals(Difficulty.HARD, Difficulty.fromPoints(3));
        assertEquals(Difficulty.HARD, Difficulty.fromPoints(10));
    }
}