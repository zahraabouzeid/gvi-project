package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FillInBlankQuestionTest {

    @Test
    void shouldFlattenBlankOptionsIntoAnswers() {
        FillInBlankQuestion.Blank firstBlank = new FillInBlankQuestion.Blank(
                "SELECT",
                List.of(new Answer("*", 2), new Answer("name", 0)),
                "FROM"
        );
        FillInBlankQuestion.Blank secondBlank = new FillInBlankQuestion.Blank(
                "users",
                List.of(new Answer("WHERE", 2)),
                ";"
        );

        FillInBlankQuestion question = new FillInBlankQuestion(
                9,
                TopicArea.DATENBANKEN_MODELLIERUNG,
                "Intro",
                "Lückentext",
                List.of(firstBlank, secondBlank),
                Difficulty.MEDIUM
        );

        assertEquals(2, question.getBlanks().size());
        assertEquals(3, question.getAnswers().size());
        assertEquals(10, question.getMaxPoints());
        assertEquals("*", question.getAnswers().getFirst().text());
    }

    @Test
    void blankShouldDefensivelyCopyOptions() {
        FillInBlankQuestion.Blank blank = new FillInBlankQuestion.Blank(
                "",
                List.of(new Answer("value", 1)),
                ""
        );

        assertThrows(UnsupportedOperationException.class, () -> blank.options().add(new Answer("other", 0)));
    }
}