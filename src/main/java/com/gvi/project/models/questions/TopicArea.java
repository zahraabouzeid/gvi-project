package com.gvi.project.models.questions;

public enum TopicArea {
    SQL_GRUNDLAGEN("Datenbank - SQL"),
    SELECT_ABFRAGEN("Wirtschaft"),
    NORMALISIERUNG("Recht"),
    ER_MODELLIERUNG("Datenbanken Modellierung / UML"),
    DDL_DML("Programmierung Pseudocode"),
    JOINS_SUBQUERIES("Maschinelles Lernen");

    private final String displayName;

    TopicArea(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
