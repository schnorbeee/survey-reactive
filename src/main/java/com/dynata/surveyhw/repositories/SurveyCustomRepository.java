package com.dynata.surveyhw.repositories;

import com.dynata.surveyhw.dtos.SurveyStatisticDto;
import com.dynata.surveyhw.entities.Survey;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface SurveyCustomRepository {

    Flux<Survey> findByMemberIdAndIsCompleted(Long memberId, Pageable pageable);

    Flux<SurveyStatisticDto> findSurveyStatisticDtos(Pageable pageable);
}
