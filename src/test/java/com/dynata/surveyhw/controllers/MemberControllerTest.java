package com.dynata.surveyhw.controllers;

import com.dynata.surveyhw.repositories.MemberRepository;
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
public class MemberControllerTest {

    @LocalServerPort
    int port;

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
