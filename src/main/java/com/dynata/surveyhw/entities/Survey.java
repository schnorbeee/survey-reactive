package com.dynata.surveyhw.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "survey")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Survey {

    @Id
    @Column("survey_id")
    private Long surveyId;

    private String name;

    @Column("expected_completes")
    private Integer expectedCompletes;

    @Column("completion_points")
    private Integer completionPoints;

    @Column("filtered_points")
    private Integer filteredPoints;
}
