package com.dynata.survayhw.services;

import com.dynata.survayhw.dtos.MemberDto;
import com.dynata.survayhw.mappers.MemberMapper;
import com.dynata.survayhw.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

    public Flux<MemberDto> saveMemberDtos(List<MemberDto> memberDtos) {
        List<Long> ids = memberDtos.stream()
                .map(MemberDto::getMemberId)
                .toList();

        return Flux.fromIterable(memberDtos)
                .map(memberMapper::toEntity)
                .flatMap(memberRepository::upsertMember)
                .thenMany(memberRepository.findAllById(ids))
                .map(memberMapper::toDto);
    }

    public Flux<MemberDto> getBySurveyIdAndIsCompleted(Long surveyId) {
        return memberRepository.findBySurveyIdAndIsCompleted(surveyId)
                .map(memberMapper::toDto);
    }

    public Flux<MemberDto> getByNotParticipatedInSurveyAndIsActive(Long surveyId) {
        return memberRepository.findByNotParticipatedSurveyAndIsActive(surveyId)
                .map(memberMapper::toDto);
    }
}
