package com.dynata.survayhw.controllers;

import com.dynata.survayhw.dtos.MemberDto;
import com.dynata.survayhw.services.CsvService;
import com.dynata.survayhw.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final CsvService csvService;

    private final MemberService memberService;

    @Autowired
    public MemberController(CsvService csvService, MemberService memberService) {
        this.csvService = csvService;
        this.memberService = memberService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberDto>> uploadMembersCsv(@RequestParam("file") MultipartFile file) {
        List<MemberDto> memberDtos = csvService.readFromCsv(file, MemberDto.class);
        return ResponseEntity.ok(memberService.saveMemberDtos(memberDtos));
    }

    @GetMapping(path = "/by-survey-and-completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberDto>> getBySurveyIdAndIsCompleted(@RequestParam("surveyId") Long surveyId) {
        return ResponseEntity.ok(memberService.getBySurveyIdAndIsCompleted(surveyId));
    }

    @GetMapping(path = "/by-not-participated-survey-and-active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberDto>> getByNotParticipatedInSurveyAndIsActive(
            @RequestParam("surveyId") Long surveyId) {
        return ResponseEntity.ok(memberService.getByNotParticipatedInSurveyAndIsActive(surveyId));
    }
}
