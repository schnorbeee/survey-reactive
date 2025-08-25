package com.dynata.surveyhw.dtos.openapi;

import com.dynata.surveyhw.dtos.MemberDto;
import com.dynata.surveyhw.dtos.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PageMemberDto", description = "Page of MemberDto")
public class PageMemberDto extends PageDto<MemberDto> {
    public PageMemberDto(List<MemberDto> content, int pageNumber, int pageSize, long totalElements) {
        super(content, pageNumber, pageSize, totalElements);
    }
}