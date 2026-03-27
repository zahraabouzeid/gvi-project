package com.gvi.project.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for Question database operations.
 * Spring Data JPA automatically provides implementations for standard CRUD operations.
 * Supports filtering by question type: MC (Multiple Choice), TF (True/False), GAP (Fill-in-the-blank)
 */
@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {

    /**
     * Get all questions
     */
    @Override
    List<QuestionEntity> findAll();

    /**
     * Get all questions by type
     * @param questionType the type of question (MC, TF, or GAP)
     * @return list of questions matching the specified type
     */
    List<QuestionEntity> findByQuestionType(QuestionType questionType);

    /**
     * Get all Multiple Choice questions
     */
    @Query("SELECT q FROM QuestionEntity q WHERE q.questionType = 'MC'")
    List<QuestionEntity> findAllMultipleChoice();

    /**
     * Get all True/False questions
     */
    @Query("SELECT q FROM QuestionEntity q WHERE q.questionType = 'TF'")
    List<QuestionEntity> findAllTrueFalse();

    /**
     * Get all Gap/Fill-in-the-blank questions
     */
    @Query("SELECT q FROM QuestionEntity q WHERE q.questionType = 'GAP'")
    List<QuestionEntity> findAllGapQuestions();

    /**
     * Find a question by its text
     */
    Optional<QuestionEntity> findByStartText(String startText);

    /**
     * Find all questions containing a specific keyword
     */
    List<QuestionEntity> findByStartTextContainingIgnoreCase(String keyword);

    /**
     * Find all questions by question set id
     */
    List<QuestionEntity> findByQuestionSetId(Integer questionSetId);

    /**
     * Find all questions by question set id and type
     */
    List<QuestionEntity> findByQuestionSetIdAndQuestionType(Integer questionSetId, QuestionType questionType);

    /**
     * Get all questions ordered by id
     */
    List<QuestionEntity> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"themes", "mcAnswers", "gapFields", "gapFields.options"})
    @Query("SELECT DISTINCT q FROM QuestionEntity q ORDER BY q.id ASC")
    List<QuestionEntity> findAllWithDetailsOrderByIdAsc();

    /**
     * Get all questions of a specific type ordered by id
     */
    List<QuestionEntity> findByQuestionTypeOrderByIdAsc(QuestionType questionType);
}
