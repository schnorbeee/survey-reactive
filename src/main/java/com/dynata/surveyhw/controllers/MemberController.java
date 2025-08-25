package com.dynata.surveyhw.controllers;

import com.dynata.surveyhw.dtos.MemberDto;
import com.dynata.surveyhw.dtos.csv.MemberCsvDto;
import com.dynata.surveyhw.dtos.openapi.PageMemberDto;
import com.dynata.surveyhw.handlers.responses.ExceptionResponse;
import com.dynata.surveyhw.services.CsvService;
import com.dynata.surveyhw.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final CsvService csvService;

    private final MemberService memberService;

    @Autowired(required = false)
    public MemberController(CsvService csvService, MemberService memberService) {
        this.csvService = csvService;
        this.memberService = memberService;
    }

    @Operation(summary = "Save members from csv file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MemberDto.class)))),
            @ApiResponse(responseCode = "400", description = "Runtime error: HttpStatus.BAD_REQUEST",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Fatal error: HttpStatus.INTERNAL_SERVER_ERROR",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<MemberDto>> uploadMembersCsv(@RequestPart("file") FilePart filePart) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(csvService.readFromCsv(filePart, MemberCsvDto.class).flatMapMany(memberService::saveMemberDtos));
    }

    @Operation(summary = "Get member list by surveyId, and status is: Completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageMemberDto.class),
                            examples = @ExampleObject(name = "PageMemberDtoExample",
                                    summary = "Paged response with MemberDto objects",
                                    externalValue = "/openapi/examples/page-member-example.json"
                            ))),
            @ApiResponse(responseCode = "400", description = "Runtime error: HttpStatus.BAD_REQUEST",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Fatal error: HttpStatus.INTERNAL_SERVER_ERROR",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping(path = "/by-survey-and-completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<MemberDto>> getBySurveyIdAndIsCompleted(@RequestParam("surveyId") Long surveyId) {
        return ResponseEntity.ok(memberService.getBySurveyIdAndIsCompleted(surveyId));
    }

    @Operation(summary = "Get member list by surveyId, and status is: Rejected or Not asked")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageMemberDto.class),
                            examples = @ExampleObject(name = "PageMemberDtoExample",
                                    summary = "Paged response with MemberDto objects",
                                    externalValue = "/openapi/examples/page-member-example.json"
                            ))),
            @ApiResponse(responseCode = "400", description = "Runtime error: HttpStatus.BAD_REQUEST",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Fatal error: HttpStatus.INTERNAL_SERVER_ERROR",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping(path = "/by-not-participated-survey-and-active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<MemberDto>> getByNotParticipatedInSurveyAndIsActive(
            @RequestParam("surveyId") Long surveyId) {
        return ResponseEntity.ok(memberService.getByNotParticipatedInSurveyAndIsActive(surveyId));
    }
}
