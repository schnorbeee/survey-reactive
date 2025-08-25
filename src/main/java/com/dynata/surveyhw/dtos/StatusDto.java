package com.dynata.surveyhw.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Status record")
public class StatusDto {

    @Schema(description = "statusId", example = "4")
    private Long statusId;

    @Schema(description = "name", example = "Completed")
    private String name;
}
