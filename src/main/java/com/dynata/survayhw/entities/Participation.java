package com.dynata.survayhw.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "participation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Participation {

    @Id
    private Long id;

    @Column("member_id")
    private Long memberId;

    @Column("survey_id")
    private Long surveyId;

    @Column("status_id")
    private Long statusId;

    private Integer length;
}
