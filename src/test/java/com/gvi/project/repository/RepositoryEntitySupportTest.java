package com.gvi.project.repository;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryEntitySupportTest {

    @Test
    void themeEntityShouldStoreFields() {
        ThemeEntity theme = new ThemeEntity();

        theme.setId(3);
        theme.setName("SQL Grundlagen");
        theme.setDescription("Basiswissen");

        assertEquals(3, theme.getId());
        assertEquals("SQL Grundlagen", theme.getName());
        assertEquals("Basiswissen", theme.getDescription());
    }

    @Test
    void mcAnswerEntityShouldExposeCorrectFlagAndFields() {
        QuestionEntity question = new QuestionEntity(8, QuestionType.MC, "Question");
        McAnswerEntity answer = new McAnswerEntity();

        answer.setId(4);
        answer.setQuestion(question);
        answer.setOptionText("SELECT");
        answer.setOptionOrder(2);

        assertFalse(answer.isCorrect());

        answer.setCorrect(true);

        assertEquals(4, answer.getId());
        assertSame(question, answer.getQuestion());
        assertEquals("SELECT", answer.getOptionText());
        assertEquals(2, answer.getOptionOrder());
        assertTrue(answer.isCorrect());
    }

    @Test
    void gapOptionEntityShouldExposeCorrectFlagAndParentField() {
        GapFieldEntity gapField = new GapFieldEntity();
        GapOptionEntity option = new GapOptionEntity();

        option.setId(5);
        option.setGapField(gapField);
        option.setOptionText("FROM");
        option.setOptionOrder(1);

        assertFalse(option.isCorrect());

        option.setCorrect(true);

        assertEquals(5, option.getId());
        assertSame(gapField, option.getGapField());
        assertEquals("FROM", option.getOptionText());
        assertEquals(1, option.getOptionOrder());
        assertTrue(option.isCorrect());
    }

    @Test
    void gapFieldEntityShouldStoreQuestionTextAndOptions() {
        QuestionEntity question = new QuestionEntity(10, QuestionType.GAP, "Question");
        GapOptionEntity option = new GapOptionEntity();
        Set<GapOptionEntity> options = new LinkedHashSet<>();
        options.add(option);

        GapFieldEntity gapField = new GapFieldEntity();
        gapField.setId(7);
        gapField.setQuestion(question);
        gapField.setGapIndex(2);
        gapField.setTextBefore("SELECT");
        gapField.setTextAfter("FROM table");
        gapField.setOptions(options);

        assertEquals(7, gapField.getId());
        assertSame(question, gapField.getQuestion());
        assertEquals(2, gapField.getGapIndex());
        assertEquals("SELECT", gapField.getTextBefore());
        assertEquals("FROM table", gapField.getTextAfter());
        assertSame(options, gapField.getOptions());
    }

    @Test
    void questionEntityShouldStoreExtendedRelationships() {
        ThemeEntity theme = new ThemeEntity();
        theme.setName("Theme");

        McAnswerEntity mcAnswer = new McAnswerEntity();
        GapFieldEntity gapField = new GapFieldEntity();

        QuestionEntity question = new QuestionEntity();
        question.setId(12);
        question.setQuestionSetId(99);
        question.setQuestionType(QuestionType.GAP);
        question.setStartText("Start");
        question.setImageUrl("image.png");
        question.setEndText("End");
        question.setAllowsMultiple(true);
        question.setPoints(3);
        question.setThemes(new LinkedHashSet<>(Set.of(theme)));
        question.setMcAnswers(new LinkedHashSet<>(Set.of(mcAnswer)));
        question.setGapFields(new LinkedHashSet<>(Set.of(gapField)));

        assertEquals(12, question.getId());
        assertEquals(99, question.getQuestionSetId());
        assertEquals(QuestionType.GAP, question.getQuestionType());
        assertEquals("Start", question.getStartText());
        assertEquals("image.png", question.getImageUrl());
        assertEquals("End", question.getEndText());
        assertTrue(question.getAllowsMultiple());
        assertEquals(3, question.getPoints());
        assertEquals(1, question.getThemes().size());
        assertEquals(1, question.getMcAnswers().size());
        assertEquals(1, question.getGapFields().size());
    }

    @Test
    void questionEntityShouldAllowNullOptionalFields() {
        QuestionEntity question = new QuestionEntity();

        question.setImageUrl(null);
        question.setEndText(null);
        question.setAllowsMultiple(null);
        question.setPoints(null);

        assertNull(question.getImageUrl());
        assertNull(question.getEndText());
        assertNull(question.getAllowsMultiple());
        assertNull(question.getPoints());
    }
}