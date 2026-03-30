package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopicAreaTest {

    @Test
    void shouldExposeAllConfiguredTopicDisplayNames() {
        assertEquals(7, TopicArea.values().length);
        assertEquals("Datenbank - SQL", TopicArea.DATENBANK_SQL.getDisplayName());
        assertEquals("Datenbanken Modellierung", TopicArea.DATENBANKEN_MODELLIERUNG.getDisplayName());
        assertEquals("Programmierung Pseudocode", TopicArea.PROGRAMMIERUNG_PSEUDOCODE.getDisplayName());
        assertEquals("Maschinelles Lernen", TopicArea.MASCHINELLES_LERNEN.getDisplayName());
        assertEquals("Wirtschaft", TopicArea.WIRTSCHAFT.getDisplayName());
        assertEquals("Recht", TopicArea.RECHT.getDisplayName());
        assertEquals("UML", TopicArea.UML.getDisplayName());
    }
}