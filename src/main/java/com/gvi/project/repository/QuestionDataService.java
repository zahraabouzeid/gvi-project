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
    private final SqliteQuestionLoader sqliteQuestionLoader;
    private volatile List<QuestionEntity> cachedSqliteQuestions;

    public QuestionDataService(ObjectProvider<QuestionRepository> questionRepositoryProvider,
                               ObjectProvider<SqliteQuestionLoader> sqliteQuestionLoaderProvider) {
        this.questionRepository = questionRepositoryProvider.getIfAvailable();
        this.sqliteQuestionLoader = sqliteQuestionLoaderProvider.getIfAvailable();
    }

    /**
     * Get all questions
     */
    public List<QuestionEntity> getAllQuestions() {
        if (questionRepository != null) {
            return questionRepository.findAll();
        }
        return getAllQuestionsWithDetails();
    }

    public List<QuestionEntity> getAllQuestionsWithDetails() {
        if (questionRepository != null) {
            List<QuestionEntity> questions = questionRepository.findAllWithDetailsOrderByIdAsc();
            log.info("QuestionDataService loaded {} question entities with details from repository.", questions.size());
            return questions;
        }

        List<QuestionEntity> questions = loadQuestionsFromSqlite();
        log.info("QuestionDataService loaded {} question entities with details from SQLite.", questions.size());
        return questions;
    }

    /**
     * Get all Multiple Choice (MC) questions
     */
    public List<QuestionEntity> getAllMultipleChoiceQuestions() {
        if (questionRepository != null) {
            return questionRepository.findAllMultipleChoice();
        }
        return getQuestionsByType(QuestionType.MC);
    }

    /**
     * Get all True/False (TF) questions
     */
    public List<QuestionEntity> getAllTrueFalseQuestions() {
        if (questionRepository != null) {
            return questionRepository.findAllTrueFalse();
        }
        return getQuestionsByType(QuestionType.TF);
    }

    /**
     * Get all Gap/Fill-in-the-blank (GAP) questions
     */
    public List<QuestionEntity> getAllGapQuestions() {
        if (questionRepository != null) {
            return questionRepository.findAllGapQuestions();
        }
        return getQuestionsByType(QuestionType.GAP);
    }

    /**
     * Get questions by type
     * @param questionType the type of question (MC, TF, or GAP)
     */
    public List<QuestionEntity> getQuestionsByType(QuestionType questionType) {
        if (questionRepository != null) {
            return questionRepository.findByQuestionType(questionType);
        }
        return loadQuestionsFromSqlite().stream()
                .filter(question -> question.getQuestionType() == questionType)
                .collect(Collectors.toList());
    }

    /**
     * Get questions by question set id
     */
    public List<QuestionEntity> getQuestionsByQuestionSet(Integer questionSetId) {
        if (questionRepository != null) {
            return questionRepository.findByQuestionSetId(questionSetId);
        }
        return loadQuestionsFromSqlite().stream()
                .filter(question -> Objects.equals(question.getQuestionSetId(), questionSetId))
                .collect(Collectors.toList());
    }

    /**
     * Get questions by question set id and type
     */
    public List<QuestionEntity> getQuestionsByQuestionSetAndType(Integer questionSetId, QuestionType questionType) {
        if (questionRepository != null) {
            return questionRepository.findByQuestionSetIdAndQuestionType(questionSetId, questionType);
        }
        return loadQuestionsFromSqlite().stream()
                .filter(question -> Objects.equals(question.getQuestionSetId(), questionSetId))
                .filter(question -> question.getQuestionType() == questionType)
                .collect(Collectors.toList());
    }

    /**
     * Get a single question
     */
    public Optional<QuestionEntity> getQuestion(Integer id) {
        if (questionRepository != null) {
            return questionRepository.findById(id);
        }
        return loadQuestionsFromSqlite().stream()
                .filter(question -> Objects.equals(question.getId(), id))
                .findFirst();
    }

    /**
     * Find questions by keyword in start text
     */
    public List<QuestionEntity> searchQuestions(String keyword) {
        if (questionRepository != null) {
            return questionRepository.findByStartTextContainingIgnoreCase(keyword);
        }

        String normalizedKeyword = keyword == null ? "" : keyword.toLowerCase(Locale.ROOT);
        return loadQuestionsFromSqlite().stream()
                .filter(question -> question.getStartText() != null)
                .filter(question -> question.getStartText().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Save a question
     */
    public QuestionEntity save(QuestionEntity entity) {
        if (questionRepository == null) {
            throw new UnsupportedOperationException("SQLite question loading is read-only. Saving requires a JPA repository.");
        }
        return requireRepository().save(entity);
    }

    /**
     * Delete a question
     */
    public void delete(Integer id) {
        if (questionRepository == null) {
            throw new UnsupportedOperationException("SQLite question loading is read-only. Deleting requires a JPA repository.");
        }
        requireRepository().deleteById(id);
    }

    public boolean isQuestionSourceAvailable() {
        return questionRepository != null || (sqliteQuestionLoader != null && sqliteQuestionLoader.isAvailable());
    }

    private QuestionRepository requireRepository() {
        if (questionRepository == null) {
            throw new IllegalStateException("QuestionRepository is not available in the current Spring context.");
        }
        return questionRepository;
    }

    private List<QuestionEntity> loadQuestionsFromSqlite() {
        List<QuestionEntity> current = cachedSqliteQuestions;
        if (current != null) {
            return current;
        }

        synchronized (this) {
            if (cachedSqliteQuestions == null) {
                cachedSqliteQuestions = List.copyOf(requireSqliteQuestionLoader().loadAllQuestionsWithDetails());
            }
            return cachedSqliteQuestions;
        }
    }

    private SqliteQuestionLoader requireSqliteQuestionLoader() {
        if (sqliteQuestionLoader == null) {
            throw new IllegalStateException("SqliteQuestionLoader bean is not available in the current Spring context.");
        }
        return sqliteQuestionLoader;
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
