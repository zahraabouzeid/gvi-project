package com.gvi.project.models;

import java.util.Arrays;
import java.util.List;

/**
 * Simple POJO for Question data transfer.
 * This is a data model class, not a JPA entity.
 * For database operations, use QuestionEntity from the repository package.
 *
 * Stores questions with answers and possible answer options
 */
public class Question {
    // Primary key
    private Long id;
    // The question text
    private String question;
    // The correct answer
    private String answer;
    // Answer options as CSV string in the database
    private String possibilities;

    // Default constructor
    public Question() {
    }

    /**
     * Constructor with all fields
     * @param question The question text
     * @param answer The correct answer
     * @param possibilities Answer options as CSV string
     */
    public Question(String question, String answer, String possibilities) {
        this.question = question;
        this.answer = answer;
        this.possibilities = possibilities;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Returns the raw CSV string of possibilities
     * @return Possibilities as CSV string
     */
    public String getPossibilities() {
        return possibilities;
    }

    public void setPossibilities(String possibilities) {
        this.possibilities = possibilities;
    }

    /**
     * Converts the CSV string of possibilities into a list
     * @return List of answer options
     */
    public List<String> getPossibilitiesAsList() {
        // Split CSV string and trim whitespace
        if (possibilities == null || possibilities.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(possibilities.split(","))
                .map(String::trim)
                .toList();
    }

    /**
     * Sets possibilities from a list (converts to CSV)
     * @param possibilitiesList List of answer options
     */
    public void setPossibilitiesFromList(List<String> possibilitiesList) {
        this.possibilities = String.join(",", possibilitiesList);
    }
}


