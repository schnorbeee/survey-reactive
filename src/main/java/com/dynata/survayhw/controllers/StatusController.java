package com.dynata.survayhw.controllers;

import com.dynata.survayhw.dtos.StatusDto;
import com.dynata.survayhw.services.CsvService;
import com.dynata.survayhw.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
    public Flux<StatusDto> uploadStatusesCsv(@RequestPart("file") FilePart filePart) {
        return csvService.readFromCsv(filePart, StatusDto.class)
                .flatMapMany(statusService::saveStatusDtos);
    }
}
