package com.dynata.surveyhw.mappers;

import com.dynata.surveyhw.dtos.MemberDto;
import com.dynata.surveyhw.dtos.csv.MemberCsvDto;
import com.dynata.surveyhw.entities.Member;
import io.r2dbc.spi.Row;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberDto toDto(Member entity);

    Member toEntity(MemberCsvDto dto);

    default Member fromRow(Row row) {
        return new Member(
                row.get("member_id", Long.class),
                row.get("full_name", String.class),
                row.get("email_address", String.class),
                row.get("is_active", Boolean.class)
        );
    }

}
