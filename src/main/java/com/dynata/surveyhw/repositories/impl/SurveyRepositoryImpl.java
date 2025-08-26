package com.dynata.surveyhw.repositories.impl;

import com.dynata.surveyhw.dtos.SurveyStatisticDto;
import com.dynata.surveyhw.entities.Survey;
import com.dynata.surveyhw.mappers.SurveyMapper;
import com.dynata.surveyhw.repositories.SurveyCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Map;

@RequiredArgsConstructor
@Repository
public class SurveyRepositoryImpl implements SurveyCustomRepository {

    private final DatabaseClient client;

    private final SurveyMapper surveyMapper;

    private static final Map<String, String> SORT_COLUMN_MAP = Map.of(
            "surveyId", "survey_id",
            "name", "name",
            "expectedCompletes", "expected_completes",
            "completionPoints", "completion_points",
            "filteredPoints", "filtered_points"
    );

    @Override
    public Flux<Survey> findByMemberIdAndIsCompleted(Long memberId, Pageable pageable) {
        String baseQuery = "SELECT s.* "
                + " FROM survey s JOIN participation p ON s.survey_id = p.survey_id "
                + " WHERE p.member_id = " + memberId + " AND p.status_id = 4 ";

        String finalQuery = baseQuery + getSortClause(pageable)
                + " OFFSET " + (int) pageable.getOffset() + " "
                + " LIMIT " + (int) pageable.getPageSize();

        return getSurveys(finalQuery);
    }

    @Override
    public Flux<SurveyStatisticDto> findSurveyStatisticDtos(Pageable pageable) {
        String baseQuery = "SELECT s.survey_id AS surveyId, "
                + " s.name AS surveyName, "
                + " COUNT(CASE WHEN p.status_id = 4 THEN 1 END) AS numberOfCompletes, "
                + " COUNT(CASE WHEN p.status_id = 3 THEN 1 END) AS numberOfFilteredParticipants, "
                + " COUNT(CASE WHEN p.status_id = 2 THEN 1 END) AS numberOfRejectedParticipants, "
                + " AVG(CASE WHEN p.status_id = 4 THEN p.length ELSE NULL END) AS averageLengthOfTimeSpentOnSurvey "
                + " FROM survey AS s JOIN participation AS p ON s.survey_id = p.survey_id "
                + " GROUP BY (s.survey_id, s.name)";

        String finalQuery = baseQuery + getSortStatisticClause(pageable)
                + " OFFSET " + (int) pageable.getOffset() + " "
                + " LIMIT " + (int) pageable.getPageSize();

        return getSurveyStatistics(finalQuery);
    }

    private String getSortClause(Pageable pageable) {
        return pageable.getSort().isSorted()
                ? " ORDER BY " + pageable.getSort().stream()
                .map(order -> {
                    String column = SORT_COLUMN_MAP.getOrDefault(order.getProperty(), "survey_id");
                    if (column == null) {
                        throw new IllegalArgumentException("Invalid sort property: " + order.getProperty());
                    }
                    return column + " " + order.getDirection().name();
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("")
                : "";
    }

    private String getSortStatisticClause(Pageable pageable) {
        return pageable.getSort().isSorted()
                ? " ORDER BY " + pageable.getSort().stream()
                .map(order -> {
                    String column = order.getProperty();
                    if (column == null) {
                        throw new IllegalArgumentException("Invalid sort property: " + order.getProperty());
                    }
                    return column + " " + order.getDirection().name();
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("")
                : "";
    }
    
    private Flux<Survey> getSurveys(String finalQuery) {
        return client.sql(finalQuery)
                .map((row, metadata) -> surveyMapper.fromRow(row))
                .all();
    }

    private Flux<SurveyStatisticDto> getSurveyStatistics(String finalQuery) {
        return client.sql(finalQuery)
                .map((row, metadata) -> surveyMapper.fromStatisticRow(row))
                .all();
    }
}
