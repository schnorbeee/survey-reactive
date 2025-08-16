package com.dynata.survayhw.repositories;

import com.dynata.survayhw.entities.Participation;
import com.dynata.survayhw.repositories.returns.SurveyStatisticAverage;
import com.dynata.survayhw.repositories.returns.SurveyStatisticCount;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ParticipationRepository extends ReactiveCrudRepository<Participation, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO participation (member_id, survey_id, status_id, length)
            VALUES (:#{#p.memberId}, :#{#p.surveyId}, :#{#p.statusId}, :#{#p.length})
            ON CONFLICT (member_id, survey_id, status_id)
            DO UPDATE SET length = EXCLUDED.length
            """)
    Mono<Void> upsertParticipation(Participation p);

    @Query(value = """
            SELECT p.survey_id AS survey_id, COUNT(p.member_id) AS member_count FROM participation p 
            WHERE p.status_id = :statusId GROUP BY p.survey_id
            """)
    Flux<SurveyStatisticCount> findStatisticCountsByStatus(@Param("statusId") Long statusId);

    @Query(value = """
            SELECT p.survey_id AS survey_id, AVG(p.length) AS completed_average FROM participation p 
            WHERE p.status_id = 4 GROUP BY p.survey_id
            """)
    Flux<SurveyStatisticAverage> findStatisticLengthByStatus();
}
