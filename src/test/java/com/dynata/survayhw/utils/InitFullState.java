package com.dynata.survayhw.utils;

import com.dynata.survayhw.dtos.MemberDto;
import com.dynata.survayhw.dtos.ParticipationDto;
import com.dynata.survayhw.dtos.StatusDto;
import com.dynata.survayhw.dtos.SurveyDto;
import com.dynata.survayhw.mappers.MemberMapper;
import com.dynata.survayhw.mappers.ParticipationMapper;
import com.dynata.survayhw.mappers.StatusMapper;
import com.dynata.survayhw.mappers.SurveyMapper;
import com.dynata.survayhw.repositories.MemberRepository;
import com.dynata.survayhw.repositories.ParticipationRepository;
import com.dynata.survayhw.repositories.StatusRepository;
import com.dynata.survayhw.repositories.SurveyRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Component
public class InitFullState {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private StatusMapper statusMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private ParticipationMapper participationMapper;

    public void deleteFullDatabase() {
        participationRepository.deleteAll().block();
        surveyRepository.deleteAll().block();
        statusRepository.deleteAll().block();
        memberRepository.deleteAll().block();
    }

    public void initAllCsv(boolean exceptParticipation) {
        Flux.fromIterable(readFromCsv(new File("src/test/resources/testfiles/Statuses.csv"), StatusDto.class))
                .map(statusMapper::toEntity)
                .flatMap(statusRepository::upsertStatus)
                .then().block();

        Flux.fromIterable(readFromCsv(new File("src/test/resources/testfiles/Members.csv"), MemberDto.class))
                .map(memberMapper::toEntity)
                .flatMap(memberRepository::upsertMember)
                .then().block();

        Flux.fromIterable(readFromCsv(new File("src/test/resources/testfiles/Surveys.csv"), SurveyDto.class))
                .map(surveyMapper::toEntity)
                .flatMap(surveyRepository::upsertSurvey)
                .then().block();

        if (!exceptParticipation) {
            Flux.fromIterable(readFromCsv(new File("src/test/resources/testfiles/Participations.csv"),
                            ParticipationDto.class))
                    .map(participationMapper::toEntity)
                    .flatMap(participationRepository::upsertParticipation)
                    .then().block();
        }
    }

    private <T> List<T> readFromCsv(File file, Class<T> clazz) {
        try (InputStream inputStream = new FileInputStream(file)) {
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            MappingIterator<T> iterator = mapper.readerFor(clazz)
                    .with(schema)
                    .readValues(inputStream);
            return iterator.readAll();
        } catch (Exception e) {
            throw new RuntimeException("Error at reading " + clazz + " CSV file: " + e.getMessage(), e);
        }
    }
}
