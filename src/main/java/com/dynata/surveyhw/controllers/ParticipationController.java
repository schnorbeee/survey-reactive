package com.dynata.surveyhw.controllers;

import com.dynata.surveyhw.dtos.ParticipationDto;
import com.dynata.surveyhw.handlers.responses.ExceptionResponse;
import com.dynata.surveyhw.services.CsvService;
import com.dynata.surveyhw.services.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/participations")
public class ParticipationController {

    private final CsvService csvService;

    private final ParticipationService participationService;

    @Autowired(required = false)
    public ParticipationController(CsvService csvService, ParticipationService participationService) {
        this.csvService = csvService;
        this.participationService = participationService;
    }

    @Operation(summary = "Save participations from csv file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ParticipationDto.class)))),
            @ApiResponse(responseCode = "400", description = "Runtime error: HttpStatus.BAD_REQUEST",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Fatal error: HttpStatus.INTERNAL_SERVER_ERROR",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ParticipationDto> uploadParticipationsCsv(@RequestPart("file") FilePart filePart) {
        return csvService.readFromCsv(filePart, ParticipationDto.class)
                .flatMapMany(participationService::saveParticipationDtos);
    }
}
