package com.dynata.surveyhw.mappers;

import com.dynata.surveyhw.dtos.SurveyDto;
import com.dynata.surveyhw.dtos.SurveyStatisticDto;
import com.dynata.surveyhw.dtos.csv.SurveyCsvDto;
import com.dynata.surveyhw.entities.Survey;
import io.r2dbc.spi.Row;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface SurveyMapper {

    SurveyDto toDto(Survey entity);

    Survey toEntity(SurveyCsvDto dto);

    default Survey fromRow(Row row) {
        return new Survey(
                row.get("survey_id", Long.class),
                row.get("name", String.class),
                row.get("expected_completes", Integer.class),
                row.get("completion_points", Integer.class),
                row.get("filtered_points", Integer.class)
        );
    }

    default SurveyStatisticDto fromStatisticRow(Row row) {
        return new SurveyStatisticDto(
                row.get("surveyId", Long.class),
                row.get("surveyName", String.class),
                row.get("numberOfCompletes", Long.class),
                row.get("numberOfFilteredParticipants", Long.class),
                row.get("numberOfRejectedParticipants", Long.class),
                row.get("averageLengthOfTimeSpentOnSurvey", BigDecimal.class)
        );
    }
}
