package com.dynata.surveyhw.dtos.openapi;

import com.dynata.surveyhw.dtos.PageDto;
import com.dynata.surveyhw.dtos.SurveyDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PageSurveyDto", description = "Page of SurveyDto")
public class PageSurveyDto extends PageDto<SurveyDto> {
    public PageSurveyDto(List<SurveyDto> content, int pageNumber, int pageSize, long totalElements) {
        super(content, pageNumber, pageSize, totalElements);
    }
}