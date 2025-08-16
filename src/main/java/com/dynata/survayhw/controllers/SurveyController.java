package com.dynata.survayhw.controllers;

import com.dynata.survayhw.dtos.SurveyDto;
import com.dynata.survayhw.dtos.SurveyStatisticDto;
import com.dynata.survayhw.services.CsvService;
import com.dynata.survayhw.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final CsvService csvService;

    private final SurveyService surveyService;

    @Autowired
    public SurveyController(CsvService csvService, SurveyService surveyService) {
        this.csvService = csvService;
        this.surveyService = surveyService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<SurveyDto> uploadSurveysCsv(@RequestPart("file") FilePart filePart) {
        return csvService.readFromCsv(filePart, SurveyDto.class)
                .flatMapMany(surveyService::saveSurveyDtos);
    }

    @GetMapping(path = "/by-member-id-and-completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<SurveyDto> getByMemberIdAndIsCompleted(@RequestParam("memberId") Long memberId) {
        return surveyService.getByMemberIdAndIsCompleted(memberId);
    }

    @GetMapping(path = "/by-member-id-completion-points", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Integer>> getSurveyCompletionPointsByMemberId(
            @RequestParam("memberId") Long memberId) {
        return surveyService.getSurveyCompletionPointsByMemberId(memberId);
    }

    @GetMapping(path = "/all-statistic", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<SurveyStatisticDto> getAllStatisticSurveys() {
        return surveyService.getAllStatisticSurveys();
    }
}
