package com.gvi.project.models.questions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopicAreaTest {

    @Test
    void shouldExposeAllConfiguredTopicDisplayNames() {
        assertEquals(11, TopicArea.values().length);
        assertEquals("SQL Grundlagen", TopicArea.SQL_GRUNDLAGEN.getDisplayName());
        assertEquals("SELECT & Abfragen", TopicArea.SELECT_ABFRAGEN.getDisplayName());
        assertEquals("Normalisierung", TopicArea.NORMALISIERUNG.getDisplayName());
        assertEquals("ER-Modellierung", TopicArea.ER_MODELLIERUNG.getDisplayName());
        assertEquals("DDL & DML", TopicArea.DDL_DML.getDisplayName());
        assertEquals("Joins & Subqueries", TopicArea.JOINS_SUBQUERIES.getDisplayName());
        assertEquals("Wirtschaft", TopicArea.WIRTSCHAFT.getDisplayName());
        assertEquals("Recht", TopicArea.RECHT.getDisplayName());
        assertEquals("Datenbanken Modellierung / UML", TopicArea.DB_MODELLIERUNG.getDisplayName());
        assertEquals("Programmierung Pseudocode", TopicArea.PSEUDOCODE.getDisplayName());
        assertEquals("Maschinelles Lernen", TopicArea.MASCHINELLES_LERNEN.getDisplayName());
    }
}