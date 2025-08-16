package com.dynata.survayhw.mappers;

import com.dynata.survayhw.dtos.ParticipationDto;
import com.dynata.survayhw.entities.Participation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipationMapper {

    ParticipationDto toDto(Participation entity);

    @Mapping(target = "id", ignore = true)
    Participation toEntity(ParticipationDto dto);
}
