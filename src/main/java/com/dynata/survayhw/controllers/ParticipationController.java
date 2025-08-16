package com.dynata.survayhw.controllers;

import com.dynata.survayhw.dtos.ParticipationDto;
import com.dynata.survayhw.services.CsvService;
import com.dynata.survayhw.services.ParticipationService;
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

    @Autowired
    public ParticipationController(CsvService csvService, ParticipationService participationService) {
        this.csvService = csvService;
        this.participationService = participationService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ParticipationDto> uploadParticipationsCsv(@RequestPart("file") FilePart filePart) {
        return csvService.readFromCsv(filePart, ParticipationDto.class)
                .flatMapMany(participationService::saveParticipationDtos);
    }
}
