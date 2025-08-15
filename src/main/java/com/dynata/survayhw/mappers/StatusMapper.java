package com.dynata.survayhw.mappers;

import com.dynata.survayhw.dtos.StatusDto;
import com.dynata.survayhw.entities.Status;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    StatusDto toDto(Status entity);

    Status toEntity(StatusDto dto);
}
