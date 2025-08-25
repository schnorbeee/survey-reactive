package com.dynata.surveyhw.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Member record")
public class MemberDto {

    @Schema(description = "memberId", example = "1")
    private Long memberId;

    @Schema(description = "fullName", example = "Gipsz Jakab")
    private String fullName;

    @Schema(description = "emailAddress", example = "gipsz.jakab@mail.hu")
    private String emailAddress;

    @Schema(description = "isActive", example = "false")
    private Boolean isActive;
}
