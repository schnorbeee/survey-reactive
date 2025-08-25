package com.dynata.surveyhw.dtos.openapi;

import com.dynata.surveyhw.dtos.PageDto;
import com.dynata.surveyhw.dtos.SurveyStatisticDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PageStatisticDto", description = "Page of SurveyStatisticDto")
public class PageStatisticDto extends PageDto<SurveyStatisticDto> {
    public PageStatisticDto(List<SurveyStatisticDto> content, int pageNumber, int pageSize, long totalElements) {
        super(content, pageNumber, pageSize, totalElements);
    }
}