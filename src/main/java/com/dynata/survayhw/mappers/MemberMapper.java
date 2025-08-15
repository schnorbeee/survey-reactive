package com.dynata.survayhw.mappers;

import com.dynata.survayhw.dtos.MemberDto;
import com.dynata.survayhw.entities.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberDto toDto(Member entity);

    Member toEntity(MemberDto dto);
}
