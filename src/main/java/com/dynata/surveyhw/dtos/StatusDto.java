package com.dynata.surveyhw.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("Status Id")
    private Long statusId;

    @Schema(description = "name", example = "Completed")
    @JsonProperty("Name")
    private String name;
}
