package com.dynata.surveyhw.controllers;

import com.dynata.surveyhw.repositories.MemberRepository;
import com.dynata.surveyhw.utils.InitFullState;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InitFullState initFullState;

    @Test
    @Order(1)
    void uploadMemberTestFile_valid() {
        RestAssured.given()
                .multiPart("file", new File("src/test/resources/testfiles/Members.csv"))
                .when()
                .post("/api/members")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("$", Matchers.hasSize(300));

        assertThat(memberRepository.findAll().collectList().block()).hasSize(300);
    }

    @Test
    @Order(2)
    void getBySurveyIdAndIsCompleted_valid() {
        initFullState.initAllCsv(false);

        RestAssured.when()
                .get("/api/members/by-survey-and-completed?surveyId=2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(14))
                .body("content[0].memberId", Matchers.equalTo(7))
                .body("content[0].fullName", Matchers.equalTo("Vada Shaeffer"))
                .body("content[0].emailAddress", Matchers.equalTo("VadaShaeffer8856@gmail.com"))
                .body("content[0].isActive", Matchers.equalTo(false));
    }

    @Test
    @Order(3)
    void getByNotParticipatedInSurveyAndIsActive_valid() {
        initFullState.initAllCsv(false);

        RestAssured.when()
                .get("/api/members/by-not-participated-survey-and-active?surveyId=2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(2))
                .body("content[0].memberId", Matchers.equalTo(49))
                .body("content[0].fullName", Matchers.equalTo("Cherryl Carolina"))
                .body("content[0].emailAddress", Matchers.equalTo("CherrylCarolina2273@gmail.com"))
                .body("content[0].isActive", Matchers.equalTo(true));
    }
}
