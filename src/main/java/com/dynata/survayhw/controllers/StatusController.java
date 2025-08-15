package com.dynata.survayhw.controllers;

import com.dynata.survayhw.dtos.StatusDto;
import com.dynata.survayhw.services.CsvService;
import com.dynata.survayhw.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    private final CsvService csvService;

    private final StatusService statusService;

    @Autowired
    public StatusController(CsvService csvService, StatusService statusService) {
        this.csvService = csvService;
        this.statusService = statusService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StatusDto>> uploadStatusesCsv(@RequestParam("file") MultipartFile file) {
        List<StatusDto> statusDtos = csvService.readFromCsv(file, StatusDto.class);
        return ResponseEntity.ok(statusService.saveStatusDtos(statusDtos));
    }
}
