package com.dynata.surveyhw.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Survey record")
public class SurveyDto {

    @Schema(description = "surveyId", example = "1")
    @JsonProperty("Survey Id")
    private Long surveyId;

    @Schema(description = "name", example = "Survey 01")
    @JsonProperty("Name")
    private String name;

    @Schema(description = "expectedCompletes", example = "10")
    @JsonProperty("Expected completes")
    private Integer expectedCompletes;

    @Schema(description = "completionPoints", example = "8")
    @JsonProperty("Completion points")
    private Integer completionPoints;

    @Schema(description = "filteredPoints", example = "4")
    @JsonProperty("Filtered points")
    private Integer filteredPoints;
}
