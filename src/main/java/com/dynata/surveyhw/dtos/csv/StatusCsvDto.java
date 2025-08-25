package com.dynata.surveyhw.dtos.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatusCsvDto {

    @JsonProperty("Status Id")
    private Long statusId;

    @JsonProperty("Name")
    private String name;
}
