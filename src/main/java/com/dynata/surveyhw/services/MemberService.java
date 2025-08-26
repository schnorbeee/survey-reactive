package com.dynata.surveyhw.services;

import com.dynata.surveyhw.dtos.MemberDto;
import com.dynata.surveyhw.dtos.PageDto;
import com.dynata.surveyhw.dtos.csv.MemberCsvDto;
import com.dynata.surveyhw.mappers.MemberMapper;
import com.dynata.surveyhw.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    @Autowired
    public MemberService(MemberRepository memberRepository, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    public Flux<MemberDto> saveMemberDtos(List<MemberCsvDto> memberDtos) {
        List<Long> ids = memberDtos.stream()
                .map(MemberCsvDto::getMemberId)
                .toList();

        return Flux.fromIterable(memberDtos)
                .map(memberMapper::toEntity)
                .flatMap(memberRepository::upsertMember)
                .thenMany(memberRepository.findAllById(ids))
                .map(memberMapper::toDto);
    }

    public Mono<PageDto<MemberDto>> getBySurveyIdAndIsCompleted(Long surveyId, Pageable pageable) {
        return memberRepository.findBySurveyIdAndIsCompleted(surveyId, pageable)
                .map(memberMapper::toDto)
                .collectList()
                .zipWith(memberRepository.totalElementCountBySurveyIdAndIsCompleted(surveyId))
                .map(tuple -> new PageDto<>(
                        tuple.getT1(),
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        tuple.getT2()
                ));
    }

    public Mono<PageDto<MemberDto>> getByNotParticipatedInSurveyAndIsActive(Long surveyId, Pageable pageable) {
        return memberRepository.findByNotParticipatedSurveyAndIsActive(surveyId, pageable)
                .map(memberMapper::toDto)
                .collectList()
                .zipWith(memberRepository.totalElementCountByNotParticipatedSurveyAndIsActive(surveyId))
                .map(tuple -> new PageDto<>(
                        tuple.getT1(),
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        tuple.getT2()
                ));
    }
}
