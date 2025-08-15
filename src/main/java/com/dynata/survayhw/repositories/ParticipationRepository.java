package com.dynata.survayhw.repositories;

import com.dynata.survayhw.entities.Participation;
import com.dynata.survayhw.repositories.returns.SurveyStatisticAverage;
import com.dynata.survayhw.repositories.returns.SurveyStatisticCount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ParticipationRepository extends CrudRepository<Participation, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO participation (member_id, survey_id, status_id, length)
            VALUES (:memberId, :surveyId, :statusId, :length)
            ON CONFLICT (member_id, survey_id, status_id)
            DO UPDATE SET length = EXCLUDED.length
            """, nativeQuery = true)
    void upsertParticipation(
            @Param("memberId") Long memberId,
            @Param("surveyId") Long surveyId,
            @Param("statusId") Long statusId,
            @Param("length") Integer length
    );

    @Query("SELECT p.surveyId AS surveyId, COUNT(p.memberId) AS count FROM Participation p "
            + "WHERE p.statusId = :statusId GROUP BY p.surveyId")
    List<SurveyStatisticCount> findStatisticCountsByStatus(@Param("statusId") Long statusId);

    @Query("SELECT p.surveyId AS surveyId, AVG(p.length) AS average FROM Participation p "
            + "WHERE p.statusId = 4 GROUP BY p.surveyId")
    List<SurveyStatisticAverage> findStatisticLengthByStatus();
}
