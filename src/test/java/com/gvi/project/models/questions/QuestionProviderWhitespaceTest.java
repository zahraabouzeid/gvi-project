package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionProviderWhitespaceTest {

    @Test
    void normalizeQuestionTextCollapsesRepeatedSpaces() throws Exception {
        QuestionProvider provider = new QuestionProvider(null);

        String normalized = invokeNormalize(provider, "  Das   ist\t\tein   Test   ");

        assertEquals("Das ist ein Test", normalized);
    }

    @Test
    void normalizeQuestionTextKeepsLineBreaksButCleansEachLine() throws Exception {
        QuestionProvider provider = new QuestionProvider(null);

        String normalized = invokeNormalize(provider, "  Erste   Zeile  \n\tZweite    Zeile   ");

        assertEquals("Erste Zeile\nZweite Zeile", normalized);
    }

    private String invokeNormalize(QuestionProvider provider, String input) throws Exception {
        Method method = QuestionProvider.class.getDeclaredMethod("normalizeQuestionText", String.class);
        method.setAccessible(true);
        return (String) method.invoke(provider, input);
    }
}
