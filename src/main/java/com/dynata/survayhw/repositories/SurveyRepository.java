package com.dynata.survayhw.repositories;

import com.dynata.survayhw.entities.Survey;
import com.dynata.survayhw.repositories.returns.SurveyStatisticName;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SurveyRepository extends ReactiveCrudRepository<Survey, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO survey (survey_id, name, expected_completes, completion_points, filtered_points)
            VALUES (:#{#s.surveyId}, :#{#s.name}, :#{#s.expectedCompletes}, :#{#s.completionPoints}, :#{#s.filteredPoints})
            ON CONFLICT (survey_id)
            DO UPDATE SET name = EXCLUDED.name,
                        expected_completes = EXCLUDED.expected_completes,
                        completion_points = EXCLUDED.completion_points,
                        filtered_points = EXCLUDED.filtered_points
            """)
    Mono<Void> upsertSurvey(Survey s);

    @Query("SELECT s.* FROM survey s JOIN participation p ON s.survey_id = p.survey_id "
            + "WHERE p.member_id = :memberId AND p.status_id = 4")
    Flux<Survey> findByMemberIdAndIsCompleted(@Param("memberId") Long memberId);

    @Query("SELECT s.* FROM survey s JOIN participation p ON s.survey_id = p.survey_id "
            + "WHERE p.member_id = :memberId")
    Flux<Survey> findCompletionPointsByMemberId(@Param("memberId") Long memberId);

    @Query(value = """
            SELECT s.survey_id AS survey_id, s.name AS survey_name FROM survey s
            """)
    Flux<SurveyStatisticName> findAllSurveyIdsWithNames();
}
