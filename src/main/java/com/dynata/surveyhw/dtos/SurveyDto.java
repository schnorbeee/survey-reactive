package com.dynata.surveyhw.dtos;

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
    private Long surveyId;

    @Schema(description = "name", example = "Survey 01")
    private String name;

    @Schema(description = "expectedCompletes", example = "10")
    private Integer expectedCompletes;

    @Schema(description = "completionPoints", example = "8")
    private Integer completionPoints;

    @Schema(description = "filteredPoints", example = "4")
    private Integer filteredPoints;
}
