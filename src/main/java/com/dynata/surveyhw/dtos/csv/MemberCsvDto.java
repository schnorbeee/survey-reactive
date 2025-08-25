package com.dynata.surveyhw.dtos.csv;

import com.dynata.surveyhw.utils.ZeroOneBooleanDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberCsvDto {

    @JsonProperty("Member Id")
    private Long memberId;

    @JsonProperty("Full name")
    private String fullName;

    @JsonProperty("E-mail address")
    private String emailAddress;

    @JsonProperty("Is Active")
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    private Boolean isActive;
}
