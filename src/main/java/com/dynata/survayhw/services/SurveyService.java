package com.dynata.survayhw.services;

import com.dynata.survayhw.dtos.SurveyDto;
import com.dynata.survayhw.dtos.SurveyStatisticDto;
import com.dynata.survayhw.mappers.SurveyMapper;
import com.dynata.survayhw.repositories.ParticipationRepository;
import com.dynata.survayhw.repositories.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<SurveyDto> saveSurveyDtos(List<SurveyDto> surveyDtos) {
        return surveyDtos.stream()
                .map(surveyMapper::toEntity)
                .map(surveyRepository::save)
                .map(surveyMapper::toDto)
                .toList();
    }

    public List<SurveyDto> getByMemberIdAndIsCompleted(Long memberId) {
        return surveyRepository.findByMemberIdAndIsCompleted(memberId).stream()
                .map(surveyMapper::toDto).toList();
    }

    public Map<String, Integer> getSurveyCompletionPointsByMemberId(Long memberId) {
        Map<String, Integer> surveyCompletionPoints = new HashMap<>();
        surveyRepository.findCompletionPointsByMemberId(memberId)
                .forEach(survey -> surveyCompletionPoints.put(survey.getName(), survey.getCompletionPoints()));
        return surveyCompletionPoints;
    }

    public List<SurveyStatisticDto> getAllStatisticSurveys() {
        Map<Long, Long> rejectedMemberCount = getCountedMaps(REJECTED_STATUS_ID);
        Map<Long, Long> filteredMemberCount = getCountedMaps(FILTERED_STATUS_ID);
        Map<Long, Long> completedMemberCount = getCountedMaps(COMPLETED_STATUS_ID);
        Map<Long, Double> completedAverageLength = getCompletedAverageMaps();
        Map<Long, String> surveyIdsWithNames = getAllSurveyIdWithNames();

        List<SurveyStatisticDto> surveyStatisticList = new ArrayList<>();
        for (Map.Entry<Long, String> survey : surveyIdsWithNames.entrySet()) {
            surveyStatisticList.add(SurveyStatisticDto.builder()
                    .surveyId(survey.getKey())
                    .surveyName(survey.getValue())
                    .numberOfCompletes(Objects.nonNull(completedMemberCount.get(survey.getKey()))
                            ? completedMemberCount.get(survey.getKey()) : 0)
                    .numberOfFilteredParticipants(Objects.nonNull(filteredMemberCount.get(survey.getKey()))
                            ? filteredMemberCount.get(survey.getKey()) : 0)
                    .numberOfRejectedParticipants(Objects.nonNull(rejectedMemberCount.get(survey.getKey()))
                            ? rejectedMemberCount.get(survey.getKey()) : 0)
                    .averageLengthOfTimeSpentOnSurvey(completedAverageLength.get(survey.getKey()))
                    .build());
        }
        return surveyStatisticList;
    }

    private Map<Long, Long> getCountedMaps(Long statusId) {
        Map<Long, Long> countedMap = new HashMap<>();
        participationRepository.findStatisticCountsByStatus(statusId).forEach(count ->
                countedMap.put(count.getSurveyId(), count.getCount()));
        return countedMap;
    }

    private Map<Long, Double> getCompletedAverageMaps() {
        Map<Long, Double> averageMap = new HashMap<>();
        participationRepository.findStatisticLengthByStatus().forEach(average ->
                averageMap.put(average.getSurveyId(), average.getAverage()));
        return averageMap;
    }

    private Map<Long, String> getAllSurveyIdWithNames() {
        Map<Long, String> allSurveyIdWithNames = new HashMap<>();
        surveyRepository.findAllSurveyIdsWithNames().forEach(name ->
                allSurveyIdWithNames.put(name.getSurveyId(), name.getName()));
        return allSurveyIdWithNames;
    }
}
