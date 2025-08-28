package com.dynata.surveyhw.repositories.impl;

import com.dynata.surveyhw.entities.Member;
import com.dynata.surveyhw.repositories.MemberCustomRepository;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Map;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberCustomRepository {

    private final DatabaseClient client;

    private static final String MEMBER_DB_ID = "member_id";
    private static final String MEMBER_DB_FULL_NAME = "full_name";
    private static final String MEMBER_DB_EMAIL_ADDRESS = "email_address";
    private static final String MEMBER_DB_IS_ACTIVE = "is_active";

    private static final Map<String, String> SORT_COLUMN_MAP = Map.of(
            "memberId", MEMBER_DB_ID,
            "fullName", MEMBER_DB_FULL_NAME,
            "emailAddress", MEMBER_DB_EMAIL_ADDRESS,
            "isActive", MEMBER_DB_IS_ACTIVE
    );

    @Override
    public Flux<Member> findBySurveyIdAndIsCompleted(Long surveyId, Pageable pageable) {
        String baseQuery = "SELECT m.* "
                + " FROM member m JOIN participation p ON m.member_id = p.member_id "
                + " WHERE p.survey_id = " + surveyId + " AND p.status_id = 4";

        String finalQuery = baseQuery + getSortClause(pageable) + getOffsetLimitClause(pageable);

        return getMembers(finalQuery);
    }

    @Override
    public Flux<Member> findByNotParticipatedSurveyAndIsActive(Long surveyId, Pageable pageable) {
        String baseQuery = "SELECT m.* "
                + " FROM member m JOIN participation p ON m.member_id = p.member_id "
                + " WHERE p.survey_id = " + surveyId + " AND (p.status_id = 1 OR p.status_id = 2) "
                + " AND m.is_active = true ";

        String finalQuery = baseQuery + getSortClause(pageable) + getOffsetLimitClause(pageable);

        return getMembers(finalQuery);
    }

    private String getSortClause(Pageable pageable) {
        return pageable.getSort().isSorted()
                ? " ORDER BY " + pageable.getSort().stream()
                .map(order -> {
                    String column = SORT_COLUMN_MAP.getOrDefault(order.getProperty(), MEMBER_DB_ID);
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

    private Flux<Member> getMembers(String finalQuery) {
        return client.sql(finalQuery)
                .map((row, metadata) -> fromRow(row))
                .all();
    }

    private Member fromRow(Row row) {
        return new Member(
                row.get(MEMBER_DB_ID, Long.class),
                row.get(MEMBER_DB_FULL_NAME, String.class),
                row.get(MEMBER_DB_EMAIL_ADDRESS, String.class),
                row.get(MEMBER_DB_IS_ACTIVE, Boolean.class)
        );
    }
}
