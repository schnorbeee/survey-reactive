package com.dynata.survayhw.services;

import com.dynata.survayhw.dtos.ParticipationDto;
import com.dynata.survayhw.mappers.ParticipationMapper;
import com.dynata.survayhw.repositories.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<ParticipationDto> saveParticipationDtos(List<ParticipationDto> participationDtos) {
        return participationDtos.stream()
                .map(participationMapper::toEntity)
                .peek(participation -> participationRepository
                        .upsertParticipation(participation.getMemberId(), participation.getSurveyId(),
                                participation.getStatusId(), participation.getLength()))
                .map(participationMapper::toDto)
                .toList();
    }
}
