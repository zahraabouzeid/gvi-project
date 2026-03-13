package com.gvi.project.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for Question data operations.
 * Provides methods to retrieve questions in various formats and filter by type.
 */
@Service
public class QuestionDataService {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionDataService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Get all questions
     */
    public List<QuestionEntity> getAllQuestions() {
        return questionRepository.findAll();
    }

    /**
     * Get all Multiple Choice (MC) questions
     */
    public List<QuestionEntity> getAllMultipleChoiceQuestions() {
        return questionRepository.findAllMultipleChoice();
    }

    /**
     * Get all True/False (TF) questions
     */
    public List<QuestionEntity> getAllTrueFalseQuestions() {
        return questionRepository.findAllTrueFalse();
    }

    /**
     * Get all Gap/Fill-in-the-blank (GAP) questions
     */
    public List<QuestionEntity> getAllGapQuestions() {
        return questionRepository.findAllGapQuestions();
    }

    /**
     * Get questions by type
     * @param questionType the type of question (MC, TF, or GAP)
     */
    public List<QuestionEntity> getQuestionsByType(QuestionType questionType) {
        return questionRepository.findByQuestionType(questionType);
    }

    /**
     * Get questions by question set id
     */
    public List<QuestionEntity> getQuestionsByQuestionSet(Long questionSetId) {
        return questionRepository.findByQuestionSetId(questionSetId);
    }

    /**
     * Get questions by question set id and type
     */
    public List<QuestionEntity> getQuestionsByQuestionSetAndType(Long questionSetId, QuestionType questionType) {
        return questionRepository.findByQuestionSetIdAndQuestionType(questionSetId, questionType);
    }

    /**
     * Get a single question
     */
    public Optional<QuestionEntity> getQuestion(Long id) {
        return questionRepository.findById(id);
    }

    /**
     * Find questions by keyword in start text
     */
    public List<QuestionEntity> searchQuestions(String keyword) {
        return questionRepository.findByStartTextContainingIgnoreCase(keyword);
    }

    /**
     * Save a question
     */
    public QuestionEntity save(QuestionEntity entity) {
        return questionRepository.save(entity);
    }

    /**
     * Delete a question
     */
    public void delete(Long id) {
        questionRepository.deleteById(id);
    }

    /**
     * DTO class for cleaner data transfer
     */
    public static class QuestionDTO {
        private final Long id;
        private final Long questionSetId;
        private final QuestionType questionType;
        private final String startText;
        private final String imageUrl;
        private final String endText;
        private final Boolean allowsMultiple;
        private final Integer points;

        public QuestionDTO(QuestionEntity entity) {
            this.id = entity.getId();
            this.questionSetId = entity.getQuestionSetId();
            this.questionType = entity.getQuestionType();
            this.startText = entity.getStartText();
            this.imageUrl = entity.getImageUrl();
            this.endText = entity.getEndText();
            this.allowsMultiple = entity.getAllowsMultiple();
            this.points = entity.getPoints();
        }

        public Long getId() {
            return id;
        }

        public Long getQuestionSetId() {
            return questionSetId;
        }

        public QuestionType getQuestionType() {
            return questionType;
        }

        public String getStartText() {
            return startText;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getEndText() {
            return endText;
        }

        public Boolean getAllowsMultiple() {
            return allowsMultiple;
        }

        public Integer getPoints() {
            return points;
        }

        @Override
        public String toString() {
            return "QuestionDTO{" +
                    "id=" + id +
                    ", questionSetId=" + questionSetId +
                    ", questionType=" + questionType +
                    ", startText='" + startText + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", endText='" + endText + '\'' +
                    ", allowsMultiple=" + allowsMultiple +
                    ", points=" + points +
                    '}';
        }
    }
}
