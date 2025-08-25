package com.dynata.surveyhw.dtos.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipationCsvDto {

    @JsonProperty("Member Id")
    private Long memberId;

    @JsonProperty("Survey Id")
    private Long surveyId;

    @JsonProperty("Status")
    private Long statusId;

    @JsonProperty("Length")
    private Integer length;
}
