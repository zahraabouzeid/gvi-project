package com.gvi.project.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionTest {

    @Test
    void gettersAndSettersShouldRoundTripValues() {
        Question question = new Question();

        question.setId(4L);
        question.setQuestion("What is SQL?");
        question.setAnswer("Structured Query Language");
        question.setPossibilities(" SELECT , INSERT , UPDATE ");

        assertEquals(4L, question.getId());
        assertEquals("What is SQL?", question.getQuestion());
        assertEquals("Structured Query Language", question.getAnswer());
        assertEquals(" SELECT , INSERT , UPDATE ", question.getPossibilities());
        assertEquals(List.of("SELECT", "INSERT", "UPDATE"), question.getPossibilitiesAsList());
    }

    @Test
    void constructorAndListConversionShouldShareCsvRepresentation() {
        Question question = new Question("Question", "Answer", "A,B");

        question.setPossibilitiesFromList(List.of("X", "Y", "Z"));

        assertEquals("Question", question.getQuestion());
        assertEquals("Answer", question.getAnswer());
        assertEquals("X,Y,Z", question.getPossibilities());
        assertEquals(List.of("X", "Y", "Z"), question.getPossibilitiesAsList());
    }

    @Test
    void getPossibilitiesAsListShouldReturnEmptyListForMissingValues() {
        assertTrue(new Question().getPossibilitiesAsList().isEmpty());
        assertTrue(new Question("Question", "Answer", "").getPossibilitiesAsList().isEmpty());
    }
}