package com.dynata.surveyhw.services;

import com.dynata.surveyhw.dtos.ParticipationDto;
import com.dynata.surveyhw.dtos.csv.ParticipationCsvDto;
import com.dynata.surveyhw.mappers.ParticipationMapper;
import com.dynata.surveyhw.repositories.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;

    private final ParticipationMapper participationMapper;

    @Autowired
    public ParticipationService(ParticipationRepository participationRepository,
            ParticipationMapper participationMapper) {
        this.participationRepository = participationRepository;
        this.participationMapper = participationMapper;
    }

    public Flux<ParticipationDto> saveParticipationDtos(List<ParticipationCsvDto> participationDtos) {
        return Flux.fromIterable(participationDtos)
                .map(participationMapper::toEntity)
                .flatMap(participationRepository::upsertParticipation)
                .thenMany(participationRepository.findAll())
                .map(participationMapper::toDto);
    }
}
