package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreCalculatorTest {

    @Test
    void calculateTrueFalsePointsShouldRespectDifficultyAndCorrectness() {
        assertEquals(1, ScoreCalculator.calculateTrueFalsePoints(Difficulty.EASY, true));
        assertEquals(-2, ScoreCalculator.calculateTrueFalsePoints(Difficulty.MEDIUM, false));
        assertEquals(3, ScoreCalculator.calculateTrueFalsePoints(Difficulty.HARD, true));
    }

    @Test
    void multipleChoicePointsShouldScaleWithCorrectOptions() {
        assertEquals(8, ScoreCalculator.calculateMultipleChoicePoints(Difficulty.MEDIUM, 2, true));
        assertEquals(-15, ScoreCalculator.calculateMultipleChoicePoints(Difficulty.HARD, 3, false));
        assertEquals(12, ScoreCalculator.calculateMultipleChoiceMaxPoints(Difficulty.HARD, 2));
    }

    @Test
    void gapPointsShouldDistributeTotalPointsAcrossBlanks() {
        assertEquals(5, ScoreCalculator.calculateGapPoints(Difficulty.HARD, 4, true));
        assertEquals(-2, ScoreCalculator.calculateGapPoints(Difficulty.EASY, 2, false));
        assertEquals(18, ScoreCalculator.calculateGapMaxPoints(Difficulty.HARD, 3));
    }
}