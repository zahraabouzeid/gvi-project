package com.gvi.project.model;

public enum TopicArea {
    SQL_GRUNDLAGEN("SQL Grundlagen"),
    SELECT_ABFRAGEN("SELECT & Abfragen"),
    NORMALISIERUNG("Normalisierung"),
    ER_MODELLIERUNG("ER-Modellierung"),
    DDL_DML("DDL & DML"),
    JOINS_SUBQUERIES("Joins & Subqueries");

    private final String displayName;

    TopicArea(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
