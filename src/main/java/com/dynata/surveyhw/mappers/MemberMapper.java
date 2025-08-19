package com.dynata.surveyhw.mappers;

import com.dynata.surveyhw.dtos.MemberDto;
import com.dynata.surveyhw.entities.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberDto toDto(Member entity);

    Member toEntity(MemberDto dto);
}
