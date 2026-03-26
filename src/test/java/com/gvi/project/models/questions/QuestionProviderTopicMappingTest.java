package com.gvi.project.models.questions;

import com.gvi.project.repository.QuestionEntity;
import com.gvi.project.repository.ThemeEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionProviderTopicMappingTest {

    @Test
    void resolveTopicAreaMapsKnownDatabaseThemeAlias() throws Exception {
        QuestionProvider provider = new QuestionProvider(null);
        QuestionEntity entity = new QuestionEntity();
        entity.setId(99);

        ThemeEntity theme = new ThemeEntity();
        theme.setName("Datenbank - SQL");
        Set<ThemeEntity> themes = new LinkedHashSet<>();
        themes.add(theme);
        entity.setThemes(themes);

        TopicArea topicArea = invokeResolveTopicArea(provider, entity);

        assertEquals(TopicArea.SQL_GRUNDLAGEN, topicArea);
    }

    @Test
    void resolveTopicAreaMapsBusinessThemeToConfiguredRoomTopic() throws Exception {
        QuestionProvider provider = new QuestionProvider(null);
        QuestionEntity entity = new QuestionEntity();
        entity.setId(2);
        entity.setThemes(Set.of(createTheme("Wirtschaft")));

        TopicArea topicArea = invokeResolveTopicArea(provider, entity);

        assertEquals(TopicArea.SELECT_ABFRAGEN, topicArea);
    }

    @Test
    void resolveTopicAreaFallsBackToSqlGrundlagenForUnknownTheme() throws Exception {
        QuestionProvider provider = new QuestionProvider(null);
        QuestionEntity entity = new QuestionEntity();
        entity.setId(3);
        entity.setThemes(Set.of(createTheme("Unbekanntes Thema")));

        TopicArea topicArea = invokeResolveTopicArea(provider, entity);

        assertEquals(TopicArea.SQL_GRUNDLAGEN, topicArea);
    }

    private ThemeEntity createTheme(String name) {
        ThemeEntity theme = new ThemeEntity();
        theme.setName(name);
        return theme;
    }

    private TopicArea invokeResolveTopicArea(QuestionProvider provider, QuestionEntity entity) throws Exception {
        Method method = QuestionProvider.class.getDeclaredMethod("resolveTopicArea", QuestionEntity.class);
        method.setAccessible(true);
        return (TopicArea) method.invoke(provider, entity);
    }
}
