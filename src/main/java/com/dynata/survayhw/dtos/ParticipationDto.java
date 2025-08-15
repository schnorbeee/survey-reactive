package com.dynata.survayhw.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipationDto {

    @JsonProperty("Member Id")
    private Long memberId;

    @JsonProperty("Survey Id")
    private Long surveyId;

    @JsonProperty("Status")
    private Long statusId;

    @JsonProperty("Length")
    private Integer length;
}
