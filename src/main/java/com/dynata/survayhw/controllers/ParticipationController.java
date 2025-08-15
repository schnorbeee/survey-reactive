package com.dynata.survayhw.controllers;

import com.dynata.survayhw.dtos.ParticipationDto;
import com.dynata.survayhw.services.CsvService;
import com.dynata.survayhw.services.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public List<ParticipationDto> uploadParticipationsCsv(@RequestParam("file") MultipartFile file) {
        List<ParticipationDto> participationDtos = csvService.readFromCsv(file, ParticipationDto.class);
        return participationService.saveParticipationDtos(participationDtos);
    }
}
