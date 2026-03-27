package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrueFalseQuestionTest {

    @Test
    void getAnswersShouldAssignPointsForTrueStatement() {
        TrueFalseQuestion question = new TrueFalseQuestion(
                4,
                TopicArea.RECHT,
                "Intro",
                "Eine Aussage",
                true,
                Difficulty.MEDIUM
        );

        List<Answer> answers = question.getAnswers();

        assertEquals(2, question.getMaxPoints());
        assertEquals("Wahr", answers.get(0).text());
        assertEquals(2, answers.get(0).points());
        assertEquals("Falsch", answers.get(1).text());
        assertEquals(-2, answers.get(1).points());
        assertEquals(2, question.evaluate(true));
        assertEquals(-2, question.evaluate(false));
    }

    @Test
    void getAnswersShouldAssignPointsForFalseStatement() {
        TrueFalseQuestion question = new TrueFalseQuestion(
                5,
                TopicArea.WIRTSCHAFT,
                null,
                "Eine andere Aussage",
                false,
                Difficulty.HARD
        );

        List<Answer> answers = question.getAnswers();

        assertEquals(-3, answers.get(0).points());
        assertEquals(3, answers.get(1).points());
        assertEquals(3, question.evaluate(false));
        assertEquals(-3, question.evaluate(true));
    }
}