package com.dynata.survayhw.repositories;

import com.dynata.survayhw.entities.Member;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<Member, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO member (member_id, full_name, email_address, is_active)
            VALUES (:#{#m.memberId}, :#{#m.fullName}, :#{#m.emailAddress}, :#{#m.isActive})
            ON CONFLICT (member_id)
            DO UPDATE SET full_name = EXCLUDED.full_name,
                          email_address = EXCLUDED.email_address,
                          is_active = EXCLUDED.is_active
            """)
    Mono<Void> upsertMember(Member m);

    @Query("SELECT m.* FROM member m JOIN participation p ON m.member_id = p.member_id "
            + " WHERE p.survey_id = :surveyId AND p.status_id = 4")
    Flux<Member> findBySurveyIdAndIsCompleted(@Param("surveyId") Long surveyId);

    @Query("SELECT m.* FROM member m JOIN participation p ON m.member_id = p.member_id "
            + "WHERE p.survey_id = :surveyId AND (p.status_id = 1 OR p.status_id = 2) "
            + "AND m.is_active = true")
    Flux<Member> findByNotParticipatedSurveyAndIsActive(@Param("surveyId") Long surveyId);
}
