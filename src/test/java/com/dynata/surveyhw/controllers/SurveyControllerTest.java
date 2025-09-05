package com.dynata.surveyhw.controllers;

import com.dynata.surveyhw.repositories.SurveyRepository;
import com.dynata.surveyhw.utils.InitFullState;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class SurveyControllerTest extends AbstractIntegrationTest {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private InitFullState initFullState;

    @Test
    @Order(1)
    void uploadSurveyTestFile_valid() {
        RestAssured.given()
                .multiPart("file", new File("src/test/resources/testfiles/Surveys.csv"))
                .when()
                .post("/api/surveys")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("$", Matchers.hasSize(100));

        assertThat(surveyRepository.findAll().collectList().block()).hasSize(100);
    }

    @Test
    @Order(2)
    void getByMemberIdAndIsCompleted_valid() {
        initFullState.initAllCsv(false);

        RestAssured.when()
                .get("/api/surveys/by-member-id-and-completed?memberId=2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(9))
                .body("content[0].surveyId", Matchers.equalTo(1))
                .body("content[0].name", Matchers.equalTo("Survey 01"))
                .body("content[0].expectedCompletes", Matchers.equalTo(30))
                .body("content[0].completionPoints", Matchers.equalTo(5))
                .body("content[0].filteredPoints", Matchers.equalTo(2));
    }

    @Test
    @Order(3)
    void getSurveyCompletionPointsByMemberId_valid() {
        initFullState.initAllCsv(false);

        RestAssured.when()
                .get("/api/surveys/by-member-id-completion-points?memberId=2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("'Survey 01'", Matchers.equalTo(5))
                .body("'Survey 18'", Matchers.equalTo(25))
                .body("'Survey 30'", Matchers.equalTo(25))
                .body("'Survey 44'", Matchers.equalTo(35))
                .body("'Survey 49'", Matchers.equalTo(35))
                .body("'Survey 51'", Matchers.equalTo(15))
                .body("'Survey 60'", Matchers.equalTo(35))
                .body("'Survey 62'", Matchers.equalTo(40))
                .body("'Survey 65'", Matchers.equalTo(20))
                .body("'Survey 82'", Matchers.equalTo(35))
                .body("'Survey 85'", Matchers.equalTo(25))
                .body("'Survey 97'", Matchers.equalTo(35));
    }

    @Test
    @Order(4)
    void getAllStatisticSurveys_valid() {
        initFullState.initAllCsv(false);

        RestAssured.when()
                .get("/api/surveys/all-statistic?page=1&sort=name")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(20))
                .body("content[1].surveyId", Matchers.equalTo(2))
                .body("content[1].name", Matchers.equalTo("Survey 02"))
                .body("content[1].numberOfCompletes", Matchers.equalTo(14))
                .body("content[1].numberOfFilteredParticipants", Matchers.equalTo(7))
                .body("content[1].numberOfRejectedParticipants", Matchers.equalTo(3))
                .body("content[1].averageLengthOfTimeSpentOnSurvey", Matchers.equalTo(15.214286F));
    }
}
