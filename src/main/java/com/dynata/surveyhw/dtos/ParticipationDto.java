package com.dynata.surveyhw.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Participation record")
public class ParticipationDto {

    @Schema(description = "memberId", example = "4")
    @JsonProperty("Member Id")
    private Long memberId;

    @Schema(description = "surveyId", example = "4")
    @JsonProperty("Survey Id")
    private Long surveyId;

    @Schema(description = "statusId", example = "4")
    @JsonProperty("Status")
    private Long statusId;

    @Schema(description = "length", example = "14")
    @JsonProperty("Length")
    private Integer length;
}
