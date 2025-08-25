package com.dynata.surveyhw.dtos.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveyCsvDto {

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
