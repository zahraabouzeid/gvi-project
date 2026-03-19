package com.gvi.project.repository;

/**
 * Enum representing the different types of questions supported in the system.
 * MC = Multiple Choice
 * TF = True/False
 * GAP = Fill in the blank/Gap
 */
public enum QuestionType {
    MC("Multiple Choice"),
    TF("Wahr/Falsch"),
    GAP("Lückentext");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

