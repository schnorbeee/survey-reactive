package com.dynata.surveyhw.repositories;

import com.dynata.surveyhw.entities.Member;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<Member, Long>, MemberCustomRepository {

    @Modifying
    @Transactional
    @Query("""
            INSERT INTO member (member_id, full_name, email_address, is_active)
            VALUES (:#{#m.memberId}, :#{#m.fullName}, :#{#m.emailAddress}, :#{#m.isActive})
            ON CONFLICT (member_id)
            DO UPDATE SET full_name = EXCLUDED.full_name,
                          email_address = EXCLUDED.email_address,
                          is_active = EXCLUDED.is_active
            """)
    Mono<Void> upsertMember(Member m);

    @Query("""
            SELECT COUNT(m.member_id) 
            FROM member AS m JOIN participation AS p ON m.member_id = p.member_id
            WHERE p.survey_id = :surveyId AND p.status_id = 4
            """)
    Mono<Long> totalElementCountBySurveyIdAndIsCompleted(@Param("surveyId") Long surveyId);

    @Query("""
            SELECT COUNT(m.member_id) 
            FROM member AS m JOIN participation AS p ON m.member_id = p.member_id
            WHERE p.survey_id = :surveyId AND m.is_active = true AND p.status_id IN (1,2)
            """)
    Mono<Long> totalElementCountByNotParticipatedSurveyAndIsActive(@Param("surveyId") Long surveyId);
}
