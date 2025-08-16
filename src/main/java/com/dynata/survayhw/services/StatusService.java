package com.dynata.survayhw.services;

import com.dynata.survayhw.dtos.StatusDto;
import com.dynata.survayhw.mappers.StatusMapper;
import com.dynata.survayhw.repositories.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class StatusService {

    private final StatusRepository statusRepository;

    private final StatusMapper statusMapper;

    @Autowired
    public StatusService(StatusRepository statusRepository,
            StatusMapper statusMapper) {
        this.statusRepository = statusRepository;
        this.statusMapper = statusMapper;
    }

    public Flux<StatusDto> saveStatusDtos(List<StatusDto> statusDtos) {
        List<Long> ids = statusDtos.stream()
                .map(StatusDto::getStatusId)
                .toList();

        return Flux.fromIterable(statusDtos)
                .map(statusMapper::toEntity)
                .flatMap(statusRepository::upsertStatus)
                .thenMany(statusRepository.findAllById(ids))
                .map(statusMapper::toDto);
    }
}
