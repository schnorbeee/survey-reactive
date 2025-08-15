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
public class StatusDto {

    @JsonProperty("Status Id")
    private Integer statusId;

    @JsonProperty("Name")
    private String name;
}
