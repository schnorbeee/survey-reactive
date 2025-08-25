package com.dynata.surveyhw.dtos;

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
    private Long memberId;

    @Schema(description = "surveyId", example = "4")
    private Long surveyId;

    @Schema(description = "statusId", example = "4")
    private Long statusId;

    @Schema(description = "length", example = "14")
    private Integer length;
}
