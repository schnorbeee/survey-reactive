package com.dynata.survayhw.services;

import com.dynata.survayhw.dtos.StatusDto;
import com.dynata.survayhw.mappers.StatusMapper;
import com.dynata.survayhw.repositories.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<StatusDto> saveStatusDtos(List<StatusDto> statusDtos) {
        return statusDtos.stream()
                .map(statusMapper::toEntity)
                .map(statusRepository::save)
                .map(statusMapper::toDto)
                .toList();
    }
}
