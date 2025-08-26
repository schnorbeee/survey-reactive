package com.dynata.surveyhw.repositories;

import com.dynata.surveyhw.entities.Participation;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
public interface ParticipationRepository extends ReactiveCrudRepository<Participation, Long> {

    @Modifying
    @Transactional
    @Query("""
            INSERT INTO participation (member_id, survey_id, status_id, length)
            VALUES (:#{#p.memberId}, :#{#p.surveyId}, :#{#p.statusId}, :#{#p.length})
            ON CONFLICT (member_id, survey_id, status_id)
            DO UPDATE SET length = EXCLUDED.length
            """)
    Mono<Void> upsertParticipation(Participation p);
}
