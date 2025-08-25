package com.dynata.surveyhw.utils;

import com.dynata.surveyhw.dtos.csv.MemberCsvDto;
import com.dynata.surveyhw.dtos.csv.ParticipationCsvDto;
import com.dynata.surveyhw.dtos.csv.StatusCsvDto;
import com.dynata.surveyhw.dtos.csv.SurveyCsvDto;
import com.dynata.surveyhw.mappers.MemberMapper;
import com.dynata.surveyhw.mappers.ParticipationMapper;
import com.dynata.surveyhw.mappers.StatusMapper;
import com.dynata.surveyhw.mappers.SurveyMapper;
import com.dynata.surveyhw.repositories.MemberRepository;
import com.dynata.surveyhw.repositories.ParticipationRepository;
import com.dynata.surveyhw.repositories.StatusRepository;
import com.dynata.surveyhw.repositories.SurveyRepository;
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
        Flux.fromIterable(readFromCsv(new File("src/test/resources/testfiles/Statuses.csv"), StatusCsvDto.class))
                .map(statusMapper::toEntity)
                .flatMap(statusRepository::upsertStatus)
                .then().block();

        Flux.fromIterable(readFromCsv(new File("src/test/resources/testfiles/Members.csv"), MemberCsvDto.class))
                .map(memberMapper::toEntity)
                .flatMap(memberRepository::upsertMember)
                .then().block();

        Flux.fromIterable(readFromCsv(new File("src/test/resources/testfiles/Surveys.csv"), SurveyCsvDto.class))
                .map(surveyMapper::toEntity)
                .flatMap(surveyRepository::upsertSurvey)
                .then().block();

        if (!exceptParticipation) {
            Flux.fromIterable(readFromCsv(new File("src/test/resources/testfiles/Participations.csv"),
                            ParticipationCsvDto.class))
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
