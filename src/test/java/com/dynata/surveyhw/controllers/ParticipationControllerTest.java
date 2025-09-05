package com.dynata.surveyhw.controllers;

import com.dynata.surveyhw.repositories.ParticipationRepository;
import com.dynata.surveyhw.utils.InitFullState;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticipationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private InitFullState initFullState;

    @Test
    void uploadParticipationTestFile_valid() {
        initFullState.initAllCsv(true);

        RestAssured.given()
                .multiPart("file", new File("src/test/resources/testfiles/Participations.csv"))
                .when()
                .post("/api/participations")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("$", Matchers.hasSize(3000));

        assertThat(participationRepository.findAll().collectList().block()).hasSize(3000);
    }
}
