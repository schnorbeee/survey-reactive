package com.dynata.surveyhw.mappers;

import com.dynata.surveyhw.dtos.ParticipationDto;
import com.dynata.surveyhw.entities.Participation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipationMapper {

    ParticipationDto toDto(Participation entity);

    @Mapping(target = "id", ignore = true)
    Participation toEntity(ParticipationDto dto);
}
