package com.dynata.surveyhw.mappers;

import com.dynata.surveyhw.dtos.StatusDto;
import com.dynata.surveyhw.entities.Status;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    StatusDto toDto(Status entity);

    Status toEntity(StatusDto dto);
}
