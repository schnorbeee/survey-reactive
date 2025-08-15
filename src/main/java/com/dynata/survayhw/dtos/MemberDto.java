package com.dynata.survayhw.dtos;

import com.dynata.survayhw.utils.ZeroOneBooleanDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberDto {

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
