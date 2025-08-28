package com.dynata.surveyhw.repositories.impl;

import com.dynata.surveyhw.dtos.SurveyStatisticDto;
import com.dynata.surveyhw.entities.Survey;
import com.dynata.surveyhw.repositories.SurveyCustomRepository;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class SurveyRepositoryImpl implements SurveyCustomRepository {

    private final DatabaseClient client;

    private static final String SURVEY_DB_ID = "survey_id";
    private static final String SURVEY_DB_NAME = "name";
    private static final String SURVEY_DB_EXPECTED_COMPLETES = "expected_completes";
    private static final String SURVEY_DB_COMPLETION_POINTS = "completion_points";
    private static final String SURVEY_DB_FILTERED_POINTS = "filtered_points";

    private static final String STATISTIC_DB_ID = "surveyId";
    private static final String STATISTIC_DB_NAME = "surveyName";
    private static final String STATISTIC_DB_NUMBER_OF_COMPLETES = "numberOfCompletes";
    private static final String STATISTIC_DB_NUMBER_OF_FILTERED = "numberOfFilteredParticipants";
    private static final String STATISTIC_DB_NUMBER_OF_REJECTED = "numberOfRejectedParticipants";
    private static final String STATISTIC_DB_AVERAGE_LENGTH = "averageLengthOfTimeSpentOnSurvey";

    private static final Map<String, String> SORT_COLUMN_MAP = Map.of(
            "surveyId", SURVEY_DB_ID,
            "name", SURVEY_DB_NAME,
            "expectedCompletes", SURVEY_DB_EXPECTED_COMPLETES,
            "completionPoints", SURVEY_DB_COMPLETION_POINTS,
            "filteredPoints", SURVEY_DB_FILTERED_POINTS
    );

    @Override
    public Flux<Survey> findByMemberIdAndIsCompleted(Long memberId, Pageable pageable) {
        String baseQuery = "SELECT s.* "
                + " FROM survey s JOIN participation p ON s.survey_id = p.survey_id "
                + " WHERE p.member_id = " + memberId + " AND p.status_id = 4 ";

        String finalQuery = baseQuery + getSortClause(pageable, false) + getOffsetLimitClause(pageable);

        return getSurveys(finalQuery);
    }

    @Override
    public Flux<SurveyStatisticDto> findSurveyStatisticDtos(Pageable pageable) {
        String baseQuery = "SELECT s.survey_id AS " + STATISTIC_DB_ID + ", "
                + " s.name AS " + STATISTIC_DB_NAME + ", "
                + " COUNT(CASE WHEN p.status_id = 4 THEN 1 END) AS " + STATISTIC_DB_NUMBER_OF_COMPLETES + ", "
                + " COUNT(CASE WHEN p.status_id = 3 THEN 1 END) AS " + STATISTIC_DB_NUMBER_OF_FILTERED + ", "
                + " COUNT(CASE WHEN p.status_id = 2 THEN 1 END) AS " + STATISTIC_DB_NUMBER_OF_REJECTED + ", "
                + " AVG(CASE WHEN p.status_id = 4 THEN p.length ELSE NULL END) AS " + STATISTIC_DB_AVERAGE_LENGTH + " "
                + " FROM survey AS s JOIN participation AS p ON s.survey_id = p.survey_id "
                + " GROUP BY (s.survey_id, s.name)";

        String finalQuery = baseQuery + getSortClause(pageable, true) + getOffsetLimitClause(pageable);

        return getSurveyStatistics(finalQuery);
    }

    private String getSortClause(Pageable pageable, boolean isStatistic) {
        return pageable.getSort().isSorted()
                ? " ORDER BY " + pageable.getSort().stream()
                .map(order -> {
                    String column = getSortNameClause(order, isStatistic);
                    if (column == null) {
                        throw new IllegalArgumentException("Invalid sort property: " + order.getProperty());
                    }
                    return column + " " + order.getDirection().name();
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("")
                : "";
    }

    private String getOffsetLimitClause(Pageable pageable) {
        return " OFFSET " + (int) pageable.getOffset() + " LIMIT " + pageable.getPageSize();
    }

    private String getSortNameClause(Sort.Order order, boolean isStatistic) {
        return isStatistic ? order.getProperty() : SORT_COLUMN_MAP.getOrDefault(order.getProperty(), SURVEY_DB_ID);
    }

    private Flux<Survey> getSurveys(String finalQuery) {
        return client.sql(finalQuery)
                .map((row, metadata) -> fromRow(row))
                .all();
    }

    private Survey fromRow(Row row) {
        return new Survey(
                row.get(SURVEY_DB_ID, Long.class),
                row.get(SURVEY_DB_NAME, String.class),
                row.get(SURVEY_DB_EXPECTED_COMPLETES, Integer.class),
                row.get(SURVEY_DB_COMPLETION_POINTS, Integer.class),
                row.get(SURVEY_DB_FILTERED_POINTS, Integer.class)
        );
    }

    private Flux<SurveyStatisticDto> getSurveyStatistics(String finalQuery) {
        return client.sql(finalQuery)
                .map((row, metadata) -> fromStatisticRow(row))
                .all();
    }

    private SurveyStatisticDto fromStatisticRow(Row row) {
        return new SurveyStatisticDto(
                row.get(STATISTIC_DB_ID, Long.class),
                row.get(STATISTIC_DB_NAME, String.class),
                row.get(STATISTIC_DB_NUMBER_OF_COMPLETES, Long.class),
                row.get(STATISTIC_DB_NUMBER_OF_FILTERED, Long.class),
                row.get(STATISTIC_DB_NUMBER_OF_REJECTED, Long.class),
                row.get(STATISTIC_DB_AVERAGE_LENGTH, BigDecimal.class)
        );
    }
}
