package com.gvi.project.models.objects;

import com.gvi.project.models.questions.Difficulty;
import com.gvi.project.models.questions.Question;
import com.gvi.project.models.questions.TopicArea;
import com.gvi.project.models.questions.TrueFalseQuestion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OBJ_QuizStationTest {

    @Test
    void selectQuestionsForStationTakesFirstNFromShuffledList() {
        List<Question> shuffledQuestions = List.of(
                createQuestion(4),
                createQuestion(1),
                createQuestion(3),
                createQuestion(2)
        );

        List<Question> selected = OBJ_QuizStation.selectQuestionsForStation(shuffledQuestions, 2);

        assertEquals(2, selected.size());
        assertEquals(4, selected.get(0).getId());
        assertEquals(1, selected.get(1).getId());
    }

    @Test
    void selectQuestionsForStationReturnsAllWhenAvailableIsLessThanMax() {
        List<Question> shuffledQuestions = List.of(
                createQuestion(10),
                createQuestion(11)
        );

        List<Question> selected = OBJ_QuizStation.selectQuestionsForStation(shuffledQuestions, 10);

        assertEquals(2, selected.size());
        assertEquals(10, selected.get(0).getId());
        assertEquals(11, selected.get(1).getId());
    }

    @Test
    void selectQuestionsForStationHandlesNullAndEmptySafely() {
        List<Question> selectedFromNull = OBJ_QuizStation.selectQuestionsForStation(null, 5);
        List<Question> selectedFromEmpty = OBJ_QuizStation.selectQuestionsForStation(List.of(), 5);

        assertTrue(selectedFromNull.isEmpty());
        assertTrue(selectedFromEmpty.isEmpty());
    }

    @Test
    void selectQuestionsForStationReturnsAllWhenMaxIsNonPositive() {
        List<Question> shuffledQuestions = List.of(
                createQuestion(7),
                createQuestion(8)
        );

        List<Question> selected = OBJ_QuizStation.selectQuestionsForStation(shuffledQuestions, 0);

        assertEquals(2, selected.size());
        assertEquals(7, selected.get(0).getId());
        assertEquals(8, selected.get(1).getId());
    }

    private Question createQuestion(int id) {
        return new TrueFalseQuestion(
                id,
                TopicArea.DATENBANK_SQL,
                "intro",
                "question",
                true,
                Difficulty.EASY
        );
    }
}
