package com.dynata.survayhw.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Member {

    @Id
    @Column("member_id")
    private Long memberId;

    @Column("full_name")
    private String fullName;

    @Column("email_address")
    private String emailAddress;

    @Column("is_active")
    private Boolean isActive;
}
