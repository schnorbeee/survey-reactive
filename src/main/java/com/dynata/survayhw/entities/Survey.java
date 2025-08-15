package com.dynata.survayhw.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Survey {

    @Id
    @Column(name = "survey_id", nullable = false, unique = true)
    private Long surveyId;

    @Column(nullable = false)
    private String name;

    @Column(name = "expected_completes", nullable = false)
    private Integer expectedCompletes;

    @Column(name = "completion_points", nullable = false)
    private Integer completionPoints;

    @Column(name = "filtered_points", nullable = false)
    private Integer filteredPoints;
}
