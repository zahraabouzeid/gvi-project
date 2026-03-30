package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultipleChoiceQuestionTest {

    @Test
    void shouldExposeQuestionMetadataAndMaxPoints() {
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("A", 0));
        answers.add(new Answer("B", 3));
        answers.add(new Answer("C", 3));

        MultipleChoiceQuestion question = new MultipleChoiceQuestion(
                7,
                TopicArea.DATENBANK_SQL,
                "Intro",
                "Frage",
                answers,
                true,
                Difficulty.HARD
        );

        answers.add(new Answer("D", 3));

        assertEquals(7, question.getId());
        assertEquals(TopicArea.DATENBANK_SQL, question.getTopicArea());
        assertEquals("Intro", question.getIntroText());
        assertEquals("Frage", question.getQuestionText());
        assertEquals(QuestionType.MULTIPLE_CHOICE, question.getType());
        assertEquals(Difficulty.HARD, question.getDifficulty());
        assertTrue(question.isAllowMultipleSelection());
        assertEquals(2, question.getNumberOfCorrectOptions());
        assertEquals(12, question.getMaxPoints());
        assertEquals(3, question.getAnswers().size());
    }

    @Test
    void getAnswersShouldReturnImmutableCopy() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(
                1,
                TopicArea.DATENBANK_SQL,
                null,
                "Frage",
                List.of(new Answer("A", 1)),
                false,
                Difficulty.EASY
        );

        assertThrows(UnsupportedOperationException.class, () -> question.getAnswers().add(new Answer("B", 0)));
    }
}