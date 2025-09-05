package com.dynata.surveyhw.controllers;

import com.dynata.surveyhw.repositories.StatusRepository;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusControllerTest extends AbstractIntegrationTest {

    @Autowired
    private StatusRepository statusRepository;

    @Test
    void uploadStatusTestFile_valid() {
        RestAssured.given()
                .multiPart("file", new File("src/test/resources/testfiles/Statuses.csv"))
                .when()
                .post("/api/statuses")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("$", Matchers.hasSize(4));

        assertThat(statusRepository.findAll().collectList().block()).hasSize(4);
    }
}
