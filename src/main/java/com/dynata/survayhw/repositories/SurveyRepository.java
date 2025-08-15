package com.dynata.survayhw.repositories;

import com.dynata.survayhw.entities.Survey;
import com.dynata.survayhw.repositories.returns.SurveyStatisticName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends CrudRepository<Survey, Long> {

    @Query("SELECT s FROM Survey s JOIN Participation p ON s.surveyId = p.surveyId "
            + "WHERE p.memberId = :memberId AND p.statusId = 4")
    List<Survey> findByMemberIdAndIsCompleted(@Param("memberId") Long memberId);

    @Query("SELECT s FROM Survey s JOIN Participation p ON s.surveyId = p.surveyId "
            + "WHERE p.memberId = :memberId")
    List<Survey> findCompletionPointsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT s.surveyId AS surveyId, s.name AS name FROM Survey s")
    List<SurveyStatisticName> findAllSurveyIdsWithNames();
}
