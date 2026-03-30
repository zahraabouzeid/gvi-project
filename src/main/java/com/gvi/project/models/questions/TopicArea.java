package com.gvi.project.models.questions;

public enum TopicArea {
    DATENBANK_SQL("Datenbank - SQL"),
    DATENBANKEN_MODELLIERUNG("Datenbanken Modellierung"),
    PROGRAMMIERUNG_PSEUDOCODE("Programmierung Pseudocode"),
    MASCHINELLES_LERNEN("Maschinelles Lernen"),
    WIRTSCHAFT("Wirtschaft"),
    RECHT("Recht"),
    UML("UML");

    private final String displayName;

    TopicArea(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
