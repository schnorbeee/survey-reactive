package com.dynata.survayhw.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SurveyStatisticDto {

    private Long surveyId;

    private String surveyName;

    private Long numberOfCompletes;

    private Long numberOfFilteredParticipants;

    private Long numberOfRejectedParticipants;

    private BigDecimal averageLengthOfTimeSpentOnSurvey;
}
