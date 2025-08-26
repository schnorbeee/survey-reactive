package com.dynata.surveyhw.services;

import com.dynata.surveyhw.dtos.PageDto;
import com.dynata.surveyhw.dtos.SurveyDto;
import com.dynata.surveyhw.dtos.SurveyStatisticDto;
import com.dynata.surveyhw.dtos.csv.SurveyCsvDto;
import com.dynata.surveyhw.entities.Survey;
import com.dynata.surveyhw.mappers.SurveyMapper;
import com.dynata.surveyhw.repositories.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;

    private final SurveyMapper surveyMapper;

    @Autowired
    public SurveyService(SurveyRepository surveyRepository,
            SurveyMapper surveyMapper) {
        this.surveyRepository = surveyRepository;
        this.surveyMapper = surveyMapper;
    }

    public Flux<SurveyDto> saveSurveyDtos(List<SurveyCsvDto> surveyDtos) {
        List<Long> ids = surveyDtos.stream()
                .map(SurveyCsvDto::getSurveyId)
                .toList();

        return Flux.fromIterable(surveyDtos)
                .map(surveyMapper::toEntity)
                .flatMap(surveyRepository::upsertSurvey)
                .thenMany(surveyRepository.findAllById(ids))
                .map(surveyMapper::toDto);
    }

    public Mono<PageDto<SurveyDto>> getByMemberIdAndIsCompleted(Long memberId, Pageable pageable) {
        return surveyRepository.findByMemberIdAndIsCompleted(memberId, pageable)
                .map(surveyMapper::toDto)
                .collectList()
                .zipWith(surveyRepository.totalElementCountByMemberIdAndIsCompleted(memberId))
                .map(tuple -> new PageDto<>(
                        tuple.getT1(),
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        tuple.getT2()
                ));
    }

    public Mono<Map<String, Integer>> getSurveyCompletionPointsByMemberId(Long memberId) {
        return surveyRepository.findCompletionPointsByMemberId(memberId)
                .collectMap(Survey::getName, Survey::getCompletionPoints);
    }

    public Mono<PageDto<SurveyStatisticDto>> getAllStatisticSurveys(Pageable pageable) {
        return surveyRepository.findSurveyStatisticDtos(pageable)
                .collectList()
                .zipWith(surveyRepository.totalElementCount())
                .map(tuple -> new PageDto<>(
                        tuple.getT1(),
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        tuple.getT2()
                ));
    }
}
