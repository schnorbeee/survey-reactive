package com.dynata.surveyhw.services;

import com.dynata.surveyhw.dtos.SurveyDto;
import com.dynata.surveyhw.dtos.SurveyStatisticDto;
import com.dynata.surveyhw.dtos.csv.SurveyCsvDto;
import com.dynata.surveyhw.entities.Survey;
import com.dynata.surveyhw.mappers.SurveyMapper;
import com.dynata.surveyhw.repositories.ParticipationRepository;
import com.dynata.surveyhw.repositories.SurveyRepository;
import com.dynata.surveyhw.repositories.returns.SurveyStatisticAverage;
import com.dynata.surveyhw.repositories.returns.SurveyStatisticCount;
import com.dynata.surveyhw.repositories.returns.SurveyStatisticName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;

    private final ParticipationRepository participationRepository;

    private final SurveyMapper surveyMapper;

    private final static Long REJECTED_STATUS_ID = 2L;
    private final static Long FILTERED_STATUS_ID = 3L;
    private final static Long COMPLETED_STATUS_ID = 4L;

    @Autowired
    public SurveyService(SurveyRepository surveyRepository,
            ParticipationRepository participationRepository,
            SurveyMapper surveyMapper) {
        this.surveyRepository = surveyRepository;
        this.participationRepository = participationRepository;
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

    public Flux<SurveyDto> getByMemberIdAndIsCompleted(Long memberId) {
        return surveyRepository.findByMemberIdAndIsCompleted(memberId)
                .map(surveyMapper::toDto);
    }

    public Mono<Map<String, Integer>> getSurveyCompletionPointsByMemberId(Long memberId) {
        return surveyRepository.findCompletionPointsByMemberId(memberId)
                .collectMap(Survey::getName, Survey::getCompletionPoints);
    }

    public Flux<SurveyStatisticDto> getAllStatisticSurveys() {
        Mono<Map<Long, Long>> rejectedMemberCountMono = getCountedMaps(REJECTED_STATUS_ID);
        Mono<Map<Long, Long>> filteredMemberCountMono = getCountedMaps(FILTERED_STATUS_ID);
        Mono<Map<Long, Long>> completedMemberCountMono = getCountedMaps(COMPLETED_STATUS_ID);
        Mono<Map<Long, BigDecimal>> completedAverageLengthMono = getCompletedAverageMaps();
        Mono<Map<Long, String>> surveyIdsWithNamesMono = getAllSurveyIdWithNames();

        return Mono.zip(rejectedMemberCountMono, filteredMemberCountMono, completedMemberCountMono,
                        completedAverageLengthMono, surveyIdsWithNamesMono)
                .flatMapMany(tuple -> {
                    Map<Long, Long> rejectedMemberCount = tuple.getT1();
                    Map<Long, Long> filteredMemberCount = tuple.getT2();
                    Map<Long, Long> completedMemberCount = tuple.getT3();
                    Map<Long, BigDecimal> completedAverageLength = tuple.getT4();
                    Map<Long, String> surveyIdsWithNames = tuple.getT5();

                    // Vegyük a kulcsokat, majd alakítsuk át SurveyStatisticDto-vá
                    return Flux.fromIterable(surveyIdsWithNames.entrySet())
                            .sort(Comparator.comparingLong(Map.Entry::getKey))
                            .map(survey -> SurveyStatisticDto.builder()
                                    .surveyId(survey.getKey())
                                    .name(survey.getValue())
                                    .numberOfCompletes(completedMemberCount.getOrDefault(survey.getKey(), 0L))
                                    .numberOfFilteredParticipants(filteredMemberCount.getOrDefault(survey.getKey(), 0L))
                                    .numberOfRejectedParticipants(rejectedMemberCount.getOrDefault(survey.getKey(), 0L))
                                    .averageLengthOfTimeSpentOnSurvey(
                                            completedAverageLength.getOrDefault(survey.getKey(), BigDecimal.ZERO))
                                    .build()
                            );
                });
    }

    private Mono<Map<Long, Long>> getCountedMaps(Long statusId) {
        return participationRepository.findStatisticCountsByStatus(statusId)
                .collectMap(SurveyStatisticCount::survey_id, SurveyStatisticCount::member_count);
    }

    private Mono<Map<Long, BigDecimal>> getCompletedAverageMaps() {
        return participationRepository.findStatisticLengthByStatus()
                .collectMap(SurveyStatisticAverage::survey_id, SurveyStatisticAverage::completed_average);
    }

    private Mono<Map<Long, String>> getAllSurveyIdWithNames() {
        return surveyRepository.findAllSurveyIdsWithNames()
                .collectMap(SurveyStatisticName::survey_id, SurveyStatisticName::survey_name);
    }
}
