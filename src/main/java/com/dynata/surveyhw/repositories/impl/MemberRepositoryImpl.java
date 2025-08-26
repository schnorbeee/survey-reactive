package com.dynata.surveyhw.repositories.impl;

import com.dynata.surveyhw.entities.Member;
import com.dynata.surveyhw.mappers.MemberMapper;
import com.dynata.surveyhw.repositories.MemberCustomRepository;
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

    private final MemberMapper memberMapper;

    private static final Map<String, String> SORT_COLUMN_MAP = Map.of(
            "memberId", "member_id",
            "fullName", "full_name",
            "emailAddress", "email_address",
            "isActive", "is_active"
    );

    @Override
    public Flux<Member> findBySurveyIdAndIsCompleted(Long surveyId, Pageable pageable) {
        String baseQuery = "SELECT m.* "
                + " FROM member m JOIN participation p ON m.member_id = p.member_id "
                + " WHERE p.survey_id = " + surveyId + " AND p.status_id = 4";

        String finalQuery = baseQuery + getSortClause(pageable)
                + " OFFSET " + (int) pageable.getOffset() + " "
                + " LIMIT " + (int) pageable.getPageSize();

        return getMembers(finalQuery);
    }

    @Override
    public Flux<Member> findByNotParticipatedSurveyAndIsActive(Long surveyId, Pageable pageable) {
        String baseQuery = "SELECT m.* "
                + " FROM member m JOIN participation p ON m.member_id = p.member_id "
                + " WHERE p.survey_id = " + surveyId + " AND (p.status_id = 1 OR p.status_id = 2) "
                + " AND m.is_active = true ";

        String finalQuery = baseQuery + getSortClause(pageable)
                + " OFFSET " + (int) pageable.getOffset() + " "
                + " LIMIT " + (int) pageable.getPageSize();

        return getMembers(finalQuery);
    }

    private String getSortClause(Pageable pageable) {
        return pageable.getSort().isSorted()
                ? " ORDER BY " + pageable.getSort().stream()
                .map(order -> {
                    String column = SORT_COLUMN_MAP.getOrDefault(order.getProperty(), "member_id");
                    if (column == null) {
                        throw new IllegalArgumentException("Invalid sort property: " + order.getProperty());
                    }
                    return column + " " + order.getDirection().name();
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("")
                : "";
    }

    private Flux<Member> getMembers(String finalQuery) {
        return client.sql(finalQuery)
                .map((row, metadata) -> memberMapper.fromRow(row))
                .all();
    }

}
