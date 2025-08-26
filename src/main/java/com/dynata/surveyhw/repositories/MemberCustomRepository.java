package com.dynata.surveyhw.repositories;

import com.dynata.surveyhw.entities.Member;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface MemberCustomRepository {

    Flux<Member> findBySurveyIdAndIsCompleted(Long surveyId, Pageable pageable);

    Flux<Member> findByNotParticipatedSurveyAndIsActive(Long surveyId, Pageable pageable);
}
