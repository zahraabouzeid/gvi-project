package com.gvi.project.repository;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for Question data operations.
 * Provides methods to retrieve questions in various formats and filter by type.
 */
@Service
public class QuestionDataService {

    private static final Logger log = LoggerFactory.getLogger(QuestionDataService.class);

    private final QuestionRepository questionRepository;

    public QuestionDataService(ObjectProvider<QuestionRepository> questionRepositoryProvider) {
        this.questionRepository = questionRepositoryProvider.getIfAvailable();
    }

    /**
     * Get all questions
     */
    public List<QuestionEntity> getAllQuestions() {
        return requireRepository().findAll();
    }

    public List<QuestionEntity> getAllQuestionsWithDetails() {
        List<QuestionEntity> questions = requireRepository().findAllWithDetailsOrderByIdAsc();
        log.info("QuestionDataService loaded {} question entities with details from repository.", questions.size());
        return questions;
    }

    /**
     * Get all Multiple Choice (MC) questions
     */
    public List<QuestionEntity> getAllMultipleChoiceQuestions() {
        return requireRepository().findAllMultipleChoice();
    }

    /**
     * Get all True/False (TF) questions
     */
    public List<QuestionEntity> getAllTrueFalseQuestions() {
        return requireRepository().findAllTrueFalse();
    }

    /**
     * Get all Gap/Fill-in-the-blank (GAP) questions
     */
    public List<QuestionEntity> getAllGapQuestions() {
        return requireRepository().findAllGapQuestions();
    }

    /**
     * Get questions by type
     * @param questionType the type of question (MC, TF, or GAP)
     */
    public List<QuestionEntity> getQuestionsByType(QuestionType questionType) {
        return requireRepository().findByQuestionType(questionType);
    }

    /**
     * Get questions by question set id
     */
    public List<QuestionEntity> getQuestionsByQuestionSet(Integer questionSetId) {
        return requireRepository().findByQuestionSetId(questionSetId);
    }

    /**
     * Get questions by question set id and type
     */
    public List<QuestionEntity> getQuestionsByQuestionSetAndType(Integer questionSetId, QuestionType questionType) {
        return requireRepository().findByQuestionSetIdAndQuestionType(questionSetId, questionType);
    }

    /**
     * Get a single question
     */
    public Optional<QuestionEntity> getQuestion(Integer id) {
        return requireRepository().findById(id);
    }

    /**
     * Find questions by keyword in start text
     */
    public List<QuestionEntity> searchQuestions(String keyword) {
        return requireRepository().findByStartTextContainingIgnoreCase(keyword);
    }

    /**
     * Save a question
     */
    public QuestionEntity save(QuestionEntity entity) {
        return requireRepository().save(entity);
    }

    /**
     * Delete a question
     */
    public void delete(Integer id) {
        requireRepository().deleteById(id);
    }

    public boolean isRepositoryAvailable() {
        return questionRepository != null;
    }

    private QuestionRepository requireRepository() {
        if (questionRepository == null) {
            throw new IllegalStateException("QuestionRepository is not available in the current Spring context.");
        }
        return questionRepository;
    }

    /**
     * DTO class for cleaner data transfer
     */
    public static class QuestionDTO {
        private final Integer id;
        private final Integer questionSetId;
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

        public Integer getId() {
            return id;
        }

        public Integer getQuestionSetId() {
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
