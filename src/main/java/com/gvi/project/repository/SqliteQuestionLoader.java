package com.gvi.project.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class SqliteQuestionLoader {

    private static final Logger log = LoggerFactory.getLogger(SqliteQuestionLoader.class);

    private final Path databasePath;

    public SqliteQuestionLoader(@Value("${app.quiz.sqlite.path:${user.dir}/quizdb.db}") String databasePath) {
        this.databasePath = Path.of(databasePath).toAbsolutePath().normalize();
    }

    public boolean isAvailable() {
        return Files.isRegularFile(databasePath);
    }

    public Path getDatabasePath() {
        return databasePath;
    }

    public List<QuestionEntity> loadAllQuestionsWithDetails() {
        if (!isAvailable()) {
            log.warn("SQLite database file not found at {}", databasePath);
            return List.of();
        }

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + toJdbcPath(databasePath))) {
            Map<Integer, QuestionEntity> questions = loadQuestions(connection);
            if (questions.isEmpty()) {
                return List.of();
            }

            loadThemes(connection, questions);
            loadMcAnswers(connection, questions);
            Map<Integer, GapFieldEntity> gapFields = loadGapFields(connection, questions);
            loadGapOptions(connection, gapFields);

            List<QuestionEntity> loadedQuestions = new ArrayList<>(questions.values());
            log.info("Loaded {} question entities from SQLite database at {}", loadedQuestions.size(), databasePath);
            return loadedQuestions;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load questions from SQLite database at " + databasePath, exception);
        }
    }

    private Map<Integer, QuestionEntity> loadQuestions(Connection connection) throws SQLException {
        String sql = """
                SELECT question_id, question_set_id, question_type, start_text, image_url, end_text, allows_multiple, points
                FROM question
                ORDER BY question_id ASC
                """;

        Map<Integer, QuestionEntity> questions = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                QuestionEntity question = new QuestionEntity();
                question.setId(resultSet.getInt("question_id"));
                question.setQuestionSetId(resultSet.getInt("question_set_id"));
                question.setQuestionType(parseQuestionType(resultSet.getString("question_type")));
                question.setStartText(resultSet.getString("start_text"));
                question.setImageUrl(resultSet.getString("image_url"));
                question.setEndText(resultSet.getString("end_text"));
                question.setAllowsMultiple(resultSet.getInt("allows_multiple") != 0);
                question.setPoints(resultSet.getInt("points"));
                questions.put(question.getId(), question);
            }
        }
        return questions;
    }

    private void loadThemes(Connection connection, Map<Integer, QuestionEntity> questions) throws SQLException {
        String sql = """
                SELECT question_id, theme_name
                FROM question_theme_assignment
                ORDER BY question_id ASC, theme_name ASC
                """;

        int syntheticThemeId = 1;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                QuestionEntity question = questions.get(resultSet.getInt("question_id"));
                if (question == null) {
                    continue;
                }

                ThemeEntity theme = new ThemeEntity();
                theme.setId(syntheticThemeId++);
                theme.setName(resultSet.getString("theme_name"));
                theme.setDescription(null);
                question.getThemes().add(theme);
            }
        }
    }

    private void loadMcAnswers(Connection connection, Map<Integer, QuestionEntity> questions) throws SQLException {
        String sql = """
                SELECT answer_id, question_id, option_text, is_correct, option_order
                FROM mc_answer
                ORDER BY question_id ASC, option_order ASC, answer_id ASC
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                QuestionEntity question = questions.get(resultSet.getInt("question_id"));
                if (question == null) {
                    continue;
                }

                McAnswerEntity answer = new McAnswerEntity();
                answer.setId(resultSet.getInt("answer_id"));
                answer.setQuestion(question);
                answer.setOptionText(resultSet.getString("option_text"));
                answer.setCorrect(resultSet.getInt("is_correct") != 0);
                answer.setOptionOrder(resultSet.getInt("option_order"));
                question.getMcAnswers().add(answer);
            }
        }
    }

    private Map<Integer, GapFieldEntity> loadGapFields(Connection connection, Map<Integer, QuestionEntity> questions)
            throws SQLException {
        String sql = """
                SELECT gap_id, question_id, gap_index, text_before, text_after
                FROM gap_field
                ORDER BY question_id ASC, gap_index ASC, gap_id ASC
                """;

        Map<Integer, GapFieldEntity> gapFields = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                QuestionEntity question = questions.get(resultSet.getInt("question_id"));
                if (question == null) {
                    continue;
                }

                GapFieldEntity gapField = new GapFieldEntity();
                gapField.setId(resultSet.getInt("gap_id"));
                gapField.setQuestion(question);
                gapField.setGapIndex(resultSet.getInt("gap_index"));
                gapField.setTextBefore(resultSet.getString("text_before"));
                gapField.setTextAfter(resultSet.getString("text_after"));
                question.getGapFields().add(gapField);
                gapFields.put(gapField.getId(), gapField);
            }
        }
        return gapFields;
    }

    private void loadGapOptions(Connection connection, Map<Integer, GapFieldEntity> gapFields) throws SQLException {
        String sql = """
                SELECT gap_option_id, gap_id, option_text, is_correct, option_order
                FROM gap_option
                ORDER BY gap_id ASC, option_order ASC, gap_option_id ASC
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                GapFieldEntity gapField = gapFields.get(resultSet.getInt("gap_id"));
                if (gapField == null) {
                    continue;
                }

                GapOptionEntity option = new GapOptionEntity();
                option.setId(resultSet.getInt("gap_option_id"));
                option.setGapField(gapField);
                option.setOptionText(resultSet.getString("option_text"));
                option.setCorrect(resultSet.getInt("is_correct") != 0);
                option.setOptionOrder(resultSet.getInt("option_order"));
                gapField.getOptions().add(option);
            }
        }
    }

    private QuestionType parseQuestionType(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalStateException("Question type is missing in SQLite database at " + databasePath);
        }
        return QuestionType.valueOf(rawValue.trim().toUpperCase(Locale.ROOT));
    }

    private String toJdbcPath(Path path) {
        return path.toString().replace('\\', '/');
    }
}
