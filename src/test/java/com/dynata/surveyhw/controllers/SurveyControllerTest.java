package com.dynata.surveyhw.controllers;

import com.dynata.surveyhw.repositories.SurveyRepository;
import com.dynata.surveyhw.utils.InitFullState;
import io.restassured.RestAssured;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@ActiveProfiles("test")
@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = {
        "server.port=8082"
})
@DirtiesContext
public class SurveyControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private InitFullState initFullState;

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%d/%s",
                        postgres.getHost(),
                        postgres.getMappedPort(5432),
                        postgres.getDatabaseName())
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @AfterEach
    void cleanup() {
        initFullState.deleteFullDatabase();
    }

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
                .body("$", Matchers.hasSize(9))
                .body("[0].surveyId", Matchers.equalTo(1))
                .body("[0].name", Matchers.equalTo("Survey 01"))
                .body("[0].expectedCompletes", Matchers.equalTo(30))
                .body("[0].completionPoints", Matchers.equalTo(5))
                .body("[0].filteredPoints", Matchers.equalTo(2));
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
                .get("/api/surveys/all-statistic")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", Matchers.hasSize(100))
                .body("[1].surveyId", Matchers.equalTo(2))
                .body("[1].name", Matchers.equalTo("Survey 02"))
                .body("[1].numberOfCompletes", Matchers.equalTo(14))
                .body("[1].numberOfFilteredParticipants", Matchers.equalTo(7))
                .body("[1].numberOfRejectedParticipants", Matchers.equalTo(3))
                .body("[1].averageLengthOfTimeSpentOnSurvey", Matchers.equalTo(15.214286F));
    }
}
