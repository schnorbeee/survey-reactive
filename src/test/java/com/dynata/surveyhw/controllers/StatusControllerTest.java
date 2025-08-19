package com.dynata.surveyhw.controllers;

import com.dynata.surveyhw.repositories.StatusRepository;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class StatusControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private StatusRepository statusRepository;

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
        statusRepository.deleteAll().block();
    }

    @Test
    void uploadStatusTestFile_valid() {
        RestAssured.given()
                .multiPart("file", new File("src/test/resources/testfiles/Statuses.csv"))
                .when()
                .post("/api/statuses")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", Matchers.hasSize(4));

        assertThat(statusRepository.findAll().collectList().block()).hasSize(4);
    }
}
