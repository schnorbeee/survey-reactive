package com.dynata.survayhw.mappers;

import com.dynata.survayhw.dtos.SurveyDto;
import com.dynata.survayhw.entities.Survey;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SurveyMapper {

    SurveyDto toDto(Survey entity);

    Survey toEntity(SurveyDto dto);
}
