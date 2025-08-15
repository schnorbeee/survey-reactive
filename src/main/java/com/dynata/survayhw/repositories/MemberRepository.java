package com.dynata.survayhw.repositories;

import com.dynata.survayhw.entities.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {

    @Query("SELECT m FROM Member m JOIN Participation p ON m.memberId = p.memberId "
            + " WHERE p.surveyId = :surveyId AND p.statusId = 4")
    List<Member> findBySurveyIdAndIsCompleted(@Param("surveyId") Long surveyId);

    @Query("SELECT m FROM Member m JOIN Participation p ON m.memberId = p.memberId "
            + "WHERE p.surveyId = :surveyId AND (p.statusId = 1 OR p.statusId = 2) "
            + "AND m.isActive = true")
    List<Member> findByNotParticipatedSurveyAndIsActive(@Param("surveyId") Long surveyId);
}
