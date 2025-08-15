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
public class SurveyDto {

    @JsonProperty("Survey Id")
    private Long surveyId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Expected completes")
    private Integer expectedCompletes;

    @JsonProperty("Completion points")
    private Integer completionPoints;

    @JsonProperty("Filtered points")
    private Integer filteredPoints;
}
