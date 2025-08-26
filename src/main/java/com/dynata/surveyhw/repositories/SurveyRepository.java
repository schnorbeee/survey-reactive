package com.dynata.surveyhw.repositories;

import com.dynata.surveyhw.entities.Survey;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SurveyRepository extends ReactiveCrudRepository<Survey, Long>, SurveyCustomRepository {

    @Modifying
    @Transactional
    @Query("""
            INSERT INTO survey (survey_id, name, expected_completes, completion_points, filtered_points)
            VALUES (:#{#s.surveyId}, :#{#s.name}, :#{#s.expectedCompletes}, :#{#s.completionPoints}, :#{#s.filteredPoints})
            ON CONFLICT (survey_id)
            DO UPDATE SET name = EXCLUDED.name,
                        expected_completes = EXCLUDED.expected_completes,
                        completion_points = EXCLUDED.completion_points,
                        filtered_points = EXCLUDED.filtered_points
            """)
    Mono<Void> upsertSurvey(Survey s);

    @Query("""
            SELECT COUNT(s.survey_id) 
            FROM survey AS s JOIN participation AS p ON s.survey_id = p.survey_id
            WHERE p.member_id = :memberId AND p.status_id = 4
            """)
    Mono<Long> totalElementCountByMemberIdAndIsCompleted(@Param("memberId") Long memberId);

    @Query("""
            SELECT s.* 
            FROM survey AS s 
                JOIN participation AS p ON s.survey_id = p.survey_id 
            WHERE p.member_id = :memberId 
            ORDER BY s.survey_id
            """)
    Flux<Survey> findCompletionPointsByMemberId(@Param("memberId") Long memberId);

    @Query("""
            SELECT COUNT(s.survey_id) 
            FROM survey AS s
            """)
    Mono<Long> totalElementCount();
}
