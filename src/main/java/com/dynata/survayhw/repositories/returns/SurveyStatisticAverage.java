package com.dynata.survayhw.repositories.returns;

import java.math.BigDecimal;

public record SurveyStatisticAverage(Long survey_id, BigDecimal completed_average) {}
