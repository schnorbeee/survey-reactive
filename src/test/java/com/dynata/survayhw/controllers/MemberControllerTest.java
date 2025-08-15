package com.dynata.survayhw.controllers;

import com.dynata.survayhw.repositories.MemberRepository;
import com.dynata.survayhw.utils.InitFullState;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class MemberControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InitFullState initFullState;

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    @AfterEach
    void cleanup() {
        initFullState.deleteFullDatabase();
    }

    @Test
    @Order(1)
    void uploadMemberTestFile_valid() {
        RestAssured.given()
                .multiPart("file", new File("src/test/resources/testfiles/Members.csv"))
                .when()
                .post("/api/members")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", Matchers.hasSize(300));

        assertThat(memberRepository.findAll()).hasSize(300);
    }

    @Test
    @Order(2)
    void getBySurveyIdAndIsCompleted_valid() {
        initFullState.initAllCsv(false);

        RestAssured.when()
                .get("/api/members/by-survey-and-completed?surveyId=2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", Matchers.hasSize(14))
                .body("[0].'Member Id'", Matchers.equalTo(7))
                .body("[0].'Full name'", Matchers.equalTo("Vada Shaeffer"))
                .body("[0].'E-mail address'", Matchers.equalTo("VadaShaeffer8856@gmail.com"))
                .body("[0].'Is Active'", Matchers.equalTo(false));
    }

    @Test
    @Order(3)
    void getByNotParticipatedInSurveyAndIsActive_valid() {
        initFullState.initAllCsv(false);

        RestAssured.when()
                .get("/api/members/by-not-participated-survey-and-active?surveyId=2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", Matchers.hasSize(2))
                .body("[0].'Member Id'", Matchers.equalTo(49))
                .body("[0].'Full name'", Matchers.equalTo("Cherryl Carolina"))
                .body("[0].'E-mail address'", Matchers.equalTo("CherrylCarolina2273@gmail.com"))
                .body("[0].'Is Active'", Matchers.equalTo(true));
    }
}
